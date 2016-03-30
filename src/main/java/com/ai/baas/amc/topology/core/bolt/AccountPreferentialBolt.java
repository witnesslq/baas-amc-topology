package com.ai.baas.amc.topology.core.bolt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ai.baas.amc.topology.core.bean.AmcChargeBean;
import com.ai.baas.amc.topology.core.message.AMCMessageParser;
import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.core.util.KafkaProxy;
import com.ai.baas.dshm.client.CacheFactoryUtil;
import com.ai.baas.dshm.client.impl.CacheBLMapper;
import com.ai.baas.dshm.client.impl.DshmClient;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.BusinessException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

/**
 * 账务优惠bolt Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AccountPreferentialBolt extends BaseBasicBolt {
    private static final long serialVersionUID = -4549737615575118377L;

    private static Logger LOG = LoggerFactory.getLogger(AccountPreferentialBolt.class);

    private String[] outputFields = new String[] { "data" };

    private MappingRule[] mappingRules = new MappingRule[1];
    private ICacheClient cacheClient = null;
    private IDshmClient client=null;
    private KafkaProxy kafkaProxy = null;
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        LOG.info("账务优惠bolt[prepare方法]...");
        /* 1.初始化JDBC */
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        /* 2.获取报文格式信息 */
        mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT,
                BaseConstants.JDBC_DEFAULT);
        /* 3.初始化缓存*/
        if(client==null){
            client=new DshmClient();
        }
        if(cacheClient==null){
            cacheClient =  CacheFactoryUtil.getCacheClient(CacheBLMapper.CACHE_BL_CAL_PARAM);
        }
        kafkaProxy = KafkaProxy.getInstance(stormConf);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOG.info("账务优惠bolt[execute方法]...");
        try {
            String inputData = input.getString(0);
            LOG.info("输入报文：[" + inputData + "]");
            /* 1.获取并解析输入信息 */
            AMCMessageParser messageParser = AMCMessageParser.parseObject(inputData, mappingRules,
                    outputFields);
            Map<String, String> data = messageParser.getData();
            String tenantId = data.get(AmcConstants.FmtFeildName.TENANT_ID);
            String subsId = data.get(AmcConstants.FmtFeildName.SUBS_ID);
            /* 2.根据传入的详单科目查询对应的账单科目 */
            String drSubject1 = data.get(AmcConstants.FmtFeildName.SUBJECT1);
            String billSubject1 = this.queryBillSubject(tenantId,drSubject1);
            String drSubject2 = data.get(AmcConstants.FmtFeildName.SUBJECT2);
            String billSubject2 = this.queryBillSubject(tenantId,drSubject2);
            String drSubject3 = data.get(AmcConstants.FmtFeildName.SUBJECT3);
            String billSubject3 = this.queryBillSubject(tenantId,drSubject3);
            
            /* 3.累账，将记录中的数据，增加到对应账单中(记录到内存中，不沉淀到数据库) */
            List<AmcChargeBean> chargeListDB = this.queryCharge(data);
            String fee1 = data.get(AmcConstants.FmtFeildName.FEE1);
            String fee2 = data.get(AmcConstants.FmtFeildName.FEE2);
            String fee3 = data.get(AmcConstants.FmtFeildName.FEE3);
            /*累账后的结果放入该处*/
            List<AmcChargeBean> chargeListAfter = new ArrayList<AmcChargeBean>(); 
            /* 3.1 对fee1判断并累账到对应的科目 */
            if(fee1!=null && !"0".equals(fee1)){//如果费用不为0，则进行累账
                AmcChargeBean beanAfter = new AmcChargeBean();
                /*3.1.1 遍历db中的数据，找到科目1对应的数据，如果找到，则将金额加入totalAmount*/
                for(AmcChargeBean amcChargeBean :chargeListDB){
                    if(billSubject1.equals(amcChargeBean.getSubjectId())){
                        amcChargeBean.setTotalAmount(amcChargeBean.getTotalAmount()+Long.parseLong(fee1));
                        beanAfter = amcChargeBean;
                    } 
                }
                /*3.1.2 如果没有找到科目1对应的账单数据，则创建一个*/
                if(beanAfter.getAcctId()==0){
                    this.initChargeBean(beanAfter,data,Long.parseLong(fee1),Long.parseLong(billSubject1));
                }
                /*3.1.3 将结果存入处理后的list*/
                chargeListAfter.add(beanAfter);
            }
            /* 3.2 对fee2判断并累账到对应的科目 */
            if(fee2!=null && !"0".equals(fee2)){//如果费用不为0，则进行累账
                AmcChargeBean beanAfter = new AmcChargeBean();
                /*3.2.1 遍历db中的数据，找到科目1对应的数据，如果找到，则将金额加入totalAmount*/
                for(AmcChargeBean amcChargeBean :chargeListDB){
                    if(billSubject2.equals(amcChargeBean.getSubjectId())){
                        amcChargeBean.setTotalAmount(amcChargeBean.getTotalAmount()+Long.parseLong(fee2));
                        beanAfter = amcChargeBean;
                    } 
                }
                /*3.2.2 如果没有找到科目1对应的账单数据，则创建一个*/
                if(beanAfter.getAcctId()==0){
                    this.initChargeBean(beanAfter,data,Long.parseLong(fee2),Long.parseLong(billSubject2));
                }
                /*3.2.3 将结果存入处理后的list*/
                chargeListAfter.add(beanAfter);
            }
            /* 3.3 对fee3判断并累账到对应的科目 */
            if(fee3!=null && !"0".equals(fee3)){//如果费用不为0，则进行累账
                AmcChargeBean beanAfter = new AmcChargeBean();
                /*3.3.1 遍历db中的数据，找到科目1对应的数据，如果找到，则将金额加入totalAmount*/
                for(AmcChargeBean amcChargeBean :chargeListDB){
                    if(billSubject3.equals(amcChargeBean.getSubjectId())){
                        amcChargeBean.setTotalAmount(amcChargeBean.getTotalAmount()+Long.parseLong(fee3));
                        beanAfter = amcChargeBean;
                    } 
                }
                /*3.3.2 如果没有找到科目1对应的账单数据，则创建一个*/
                if(beanAfter.getAcctId()==0){
                    this.initChargeBean(beanAfter,data,Long.parseLong(fee3),Long.parseLong(billSubject3));
                }
                /*3.3.3 将结果存入处理后的list*/
                chargeListAfter.add(beanAfter);
            }
            
            /* 4.优惠计算 */
            /* 4.1 查询该账户订购的产品列表 */
            List<Map<String, String>> productList = this.queryProductList(tenantId,subsId);            
            /* 4.2 循环遍历产品列表，根据优先级，查询对应的扩展信息 */
            
            for(Map<String,String> product:productList){
                String productId = product.get(AmcConstants.ProductInfo.PRODUCT_ID);
                /* 4.3 根据扩展信息中相应的参数配置，计算优惠额度，对账单项进行优惠 */
                List<Map<String, String>> productDetailList = this.queryProductDetailList(tenantId,productId);
                for(Map<String, String> pruductDetailMap :productDetailList){
                    String calcType = pruductDetailMap.get(AmcConstants.ProductInfo.CALC_TYPE);
                    String newSubject = pruductDetailMap.get(AmcConstants.ProductInfo.NEW_SUBJECT);
                    String billSubject = pruductDetailMap.get(AmcConstants.ProductInfo.BILL_SUBJECT);
                    String refSubject = pruductDetailMap.get(AmcConstants.ProductInfo.REF_SUBJECT);
                    /*4.3.1 保底*/
                    if(AmcConstants.ProductInfo.CALC_TYPE_BD.equals(calcType)){
                        long billSubjectAmount = 0;
                        long refSubjectAmount = 0;
                        /*4.3.1.1 获取参考科目，计费科目的金额*/
                        for(AmcChargeBean amcChargeBean: chargeListAfter){
                            if(amcChargeBean.getSubjectId().equals(billSubject)){
                                billSubjectAmount=amcChargeBean.getTotalAmount(); 
                            }
                            if(amcChargeBean.getSubjectId().equals(refSubject)){
                                refSubjectAmount=amcChargeBean.getTotalAmount(); 
                            }
                        }
                        /*4.3.1.2 获取该产品保底金额*/
                        List<Map<String,String>> extList = this.queryProductExtList(tenantId, productId,AmcConstants.ProductInfo.BD_AMOUNT);
                        long bdAmount = Long.parseLong(extList.get(0).get(AmcConstants.ProductInfo.BD_AMOUNT));
                        AmcChargeBean amcChargeBean = new AmcChargeBean();
                        if(refSubjectAmount<bdAmount){//
                            /*4.3.1.3 如果参考科目金额小于保底，则将计费科目金额 */
                            this.initChargeBean(amcChargeBean, data, (bdAmount-billSubjectAmount), Long.parseLong(newSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                            this.initChargeBean(amcChargeBean, data, (billSubjectAmount), Long.parseLong(newSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                        }else{
                            /*4.3.1.4 如果参考科目金额小于保底，则将计费科目金额 */
                            this.initChargeBean(amcChargeBean, data, (0), Long.parseLong(newSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                            this.initChargeBean(amcChargeBean, data, (billSubjectAmount), Long.parseLong(newSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                        }
                    }
                    /*4.3.2 封顶*/
                    if(AmcConstants.ProductInfo.CALC_TYPE_FD.equals(calcType)){
                        long billSubjectAmount = 0;
                        long refSubjectAmount = 0;
                        /*4.3.2.1 获取参考科目，计费科目的金额*/
                        for(AmcChargeBean amcChargeBean: chargeListAfter){
                            if(amcChargeBean.getSubjectId().equals(billSubject)){
                                billSubjectAmount=amcChargeBean.getTotalAmount(); 
                            }
                            if(amcChargeBean.getSubjectId().equals(refSubject)){
                                refSubjectAmount=amcChargeBean.getTotalAmount(); 
                            }
                        }
                        /*4.3.2.2 获取该产品封顶金额*/
                        List<Map<String,String>> extList = this.queryProductExtList(tenantId, productId, AmcConstants.ProductInfo.FD_AMOUNT);
                        long fdAmount = Long.parseLong(extList.get(0).get(AmcConstants.ProductInfo.FD_AMOUNT));    
                        AmcChargeBean amcChargeBean = new AmcChargeBean();                   
                        if(refSubjectAmount > fdAmount){
                            /*4.3.2.3 如果参考科目金额大于封顶金额，则计费金额等于峰顶金额*/
                            this.initChargeBean(amcChargeBean, data, (fdAmount), Long.parseLong(billSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                        }else if(refSubjectAmount <= fdAmount){
                            /*4.3.2.4 如果参考科目金额小于或等于封顶金额，则计费金额等于计费金额*/
                            this.initChargeBean(amcChargeBean, data, (billSubjectAmount), Long.parseLong(billSubject));
                            this.saveAmcChargeBean(amcChargeBean);
                        }
                    }
                }
            }
                        
            /* 6.发送信控消息 */
            kafkaProxy.sendMessage(inputData);
            
        }catch (BusinessException e) {
            /*处理各种异常*/
            //TODO
            LOG.error("账务优惠拓扑异常：["+e.getMessage()+"]",e);
        }catch (Exception e) {
           LOG.error("账务优惠拓扑异常：["+e.getMessage()+"]",e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }
    /**
     * 查询当前账单
     * @param data
     * @return
     * @author LiangMeng
     */
    private List<AmcChargeBean> queryCharge( Map<String, String> data){
        StringBuffer sql = new StringBuffer();
        sql.append("");
        List<AmcChargeBean> duplicateCheckings = JdbcTemplate.query(sql.toString(), BaseConstants.JDBC_DEFAULT, new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
        return duplicateCheckings;
    }
    /**
     * 根据传入的详单科目查询对应的账单科目
     * 
     * @param drSubject
     * @return
     * @author LiangMeng
     */
    private String queryBillSubject(String tenantId ,String drSubject) {
        String billSubject = null;
        Map<String,String> params = new TreeMap<String,String>();
        params.put("tenant_id", tenantId); 
        params.put("dr_subject", drSubject);
        List<Map<String, String>> results=client.list("amc_dr_bill_subject_map")
             .where(params)
             .executeQuery(cacheClient);
        if(results!=null&&results.size()>0){
            billSubject = results.get(0).get("bill_subject");
        }
        return billSubject;       
    }
    /**
     * 查询用户订购产品列表
     * @param data
     * @return
     * @author LiangMeng
     */
    private List<Map<String, String>> queryProductList(String tenantId,String subsId) {        
        Map<String,String> params = new TreeMap<String,String>();
        params.put("tenant_id", tenantId); 
        List<Map<String, String>> results=client.list("user_product_info")
             .where(params)
             .executeQuery(cacheClient);
        return results;       
    }
    /**
     * 根据产品id查询产品详细信息
     * @param productId
     * @return
     * @author LiangMeng
     */
    private List<Map<String, String>> queryProductDetailList(String tenantId,String productId) {        
        Map<String,String> params = new TreeMap<String,String>();
        params.put("tenant_id", tenantId); 
        params.put("product_id", productId); 
        List<Map<String, String>> results=client.list("amc_product_detail")
             .where(params)
             .executeQuery(cacheClient);
        return results;       
    }
    /**
     * 根据产品id查询产品扩展信息
     * @param productId
     * @return
     * @author LiangMeng
     */
    private List<Map<String, String>> queryProductExtList(String tenantId,String productId,String extName) {        
        Map<String,String> params = new TreeMap<String,String>();
        params.put("tenant_id", tenantId); 
        params.put("product_id", productId); 
        params.put("ext_name", extName); 
        List<Map<String, String>> results=client.list("amc_product_ext")
             .where(params)
             .executeQuery(cacheClient);
        return results;       
    }
    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    private int saveAmcChargeBean(AmcChargeBean amcChargeBean) {
        int result = 0;
        Connection conn = null;
        try {
            StringBuffer sql = new StringBuffer();
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            result = JdbcTemplate.update(sql.toString(), conn);
        } catch (Exception e) {
            LOG.error("账务优惠拓扑异常：["+e.getMessage()+"]",e);
        }
       
        return result;
    }
    
    /**
     * 初始化账单详细表信息
     * @param amcChargeBean
     * @param data
     * @author LiangMeng
     */
    private void initChargeBean(AmcChargeBean amcChargeBean,Map<String,String> data,long fee,long subjectId){
        amcChargeBean.setAcctId(Long.parseLong(data.get(AmcConstants.FmtFeildName.ACCT_ID)));
        amcChargeBean.setBalance(fee);
        amcChargeBean.setCustId(Long.parseLong(data.get(AmcConstants.FmtFeildName.CUST_ID)));
        amcChargeBean.setDiscTotalAmount(fee);
        amcChargeBean.setLastPayDate(new Date());
        amcChargeBean.setPayStatus(1l);
        amcChargeBean.setServiceId(Long.parseLong(data.get(AmcConstants.FmtFeildName.SERVICE_ID)));
        amcChargeBean.setSubjectId(subjectId);
        amcChargeBean.setSubsId(Long.parseLong(data.get(AmcConstants.FmtFeildName.SUBS_ID)));
        amcChargeBean.setTotalAmount(fee);
    }

}
