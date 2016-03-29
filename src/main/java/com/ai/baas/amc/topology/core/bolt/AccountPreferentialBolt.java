package com.ai.baas.amc.topology.core.bolt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.dbutils.QueryRunner;
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
import com.ai.baas.dshm.client.CacheFactoryUtil;
import com.ai.baas.dshm.client.impl.CacheBLMapper;
import com.ai.baas.dshm.client.impl.DshmClient;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.duplicate.DuplicateChecking;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.util.BaseConstants;
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
            String tenantId = data.get(AmcConstants.TENANT_ID);
            /* 2.根据传入的详单科目查询对应的账单科目 */
            String drSubject1 = data.get(AmcConstants.SUBJECT1);
            String billSubject1 = this.queryBillSubject(tenantId,drSubject1);
            String drSubject2 = data.get(AmcConstants.SUBJECT2);
            String billSubject2 = this.queryBillSubject(tenantId,drSubject2);
            String drSubject3 = data.get(AmcConstants.SUBJECT3);
            String billSubject3 = this.queryBillSubject(tenantId,drSubject3);
            /* 3.累账，将记录中的数据，增加到对应账单中(记录到内存中，不沉淀到数据库) */
            String fee1 = data.get(AmcConstants.FEE1);
            if(!"0".equals(fee1)){//如果费用不为0，则进行累账
                
            }
            /* 4.优惠计算 */
            /* 4.1 根据产品信息，查询该账户订购的产品列表 */
            
            /* 4.2 循环遍历产品列表，根据优先级，查询对应的扩展信息 */
            
            /* 4.3 根据扩展信息中相应的参数配置，计算优惠额度，对账单项进行优惠 */
            
            /* 5.将计算后结果输出到账单表 */
            
            /* 6.发送信控消息 */
            
        } catch (Exception e) {
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
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    private int saveBill(String sql) {
        int result = 0;
        Connection conn = null;
        try {
            conn = conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            result = JdbcTemplate.update(sql, conn);
        } catch (Exception e) {
            LOG.error("账务优惠拓扑异常：["+e.getMessage()+"]",e);
        }
       
        return result;
    }

    /**
     * 发送信控消息
     * 
     * @return
     * @author LiangMeng
     */
    private String sendXkMsg() {
        return null;
    }

}
