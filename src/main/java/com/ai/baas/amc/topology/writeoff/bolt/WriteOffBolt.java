package com.ai.baas.amc.topology.writeoff.bolt;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ai.baas.amc.topology.core.message.AMCMessageParser;
import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.core.util.DSUtil;
import com.ai.baas.amc.topology.writeoff.service.AmcWriteOffSV;
import com.ai.baas.dshm.client.CacheFactoryUtil;
import com.ai.baas.dshm.client.impl.CacheBLMapper;
import com.ai.baas.dshm.client.impl.DshmClient;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.failbill.FailBillHandler;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.BusinessException;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

/**
 * 销账bolt
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class WriteOffBolt extends BaseBasicBolt {
    private static final long serialVersionUID = -4549737615575118377L;

    private static Logger LOG = LoggerFactory.getLogger(WriteOffBolt.class);

    private String[] outputFields = new String[] { "data" };

    private ICacheClient cacheClient = null;
    private IDshmClient client=null;
    private MappingRule[] mappingRules = new MappingRule[1];
    /*初始化dao*/
    private AmcWriteOffSV amcWriteOffSV = new AmcWriteOffSV();
    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        LOG.info("销账bolt[prepare方法]...");
        /* 1.初始化JDBC */
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        /* 2.获取报文格式信息 */
        mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT,
                BaseConstants.JDBC_DEFAULT);
        /* 3.初始化缓存*/
        if(client==null){
            client=new DshmClient();
        }
        Properties p=new Properties();
        p.setProperty(AmcConstants.CacheConfig.CCS_APPNAME, (String)stormConf.get(AmcConstants.CacheConfig.CCS_APPNAME));
        p.setProperty(AmcConstants.CacheConfig.CCS_ZK_ADDRESS, (String)stormConf.get(AmcConstants.CacheConfig.CCS_ZK_ADDRESS));
        if(cacheClient==null){
            cacheClient =  CacheFactoryUtil.getCacheClient(p,CacheBLMapper.CACHE_BL_CAL_PARAM);
        }
        /* 4.初始化序列数据源*/
        DSUtil.initSeqDS(stormConf);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        LOG.info("销账bolt[execute方法]...");
        Map<String, String> data = null;
        try {
            String inputData = input.getString(0);
            LOG.info("输入报文：[" + inputData + "]");
            /* 1.获取并解析输入信息 */
            AMCMessageParser messageParser = AMCMessageParser.parseObject(inputData, mappingRules,
                    outputFields);
            data = messageParser.getData();
            String tenantId = data.get(AmcConstants.FmtFeildName.TENANT_ID);
            String acctId = data.get(AmcConstants.FmtFeildName.ACCT_ID);
            /* 2. 执行销账*/
            boolean isSuccess = amcWriteOffSV.writeOffCore(acctId, tenantId, JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT), cacheClient, client);
            if(!isSuccess){
               throw new BusinessException(AmcConstants.FailConstant.FAIL_CODE_OWE, "销账处理失败，tenantId:["+tenantId+"],acct_id:["+acctId+"]");
            }
        }catch (BusinessException e) {
            /*处理 异常*/
            FailBillHandler.addFailBillMsg(data,AmcConstants.FailConstant.FAIL_STEP_OWE,e.getErrorCode(),e.getErrorMessage());
            LOG.error("销账拓扑异常：["+e.getMessage()+"]",e);
        }catch (Exception e) {
           LOG.error("销账拓扑异常：["+e.getMessage()+"]",e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }
   

}
