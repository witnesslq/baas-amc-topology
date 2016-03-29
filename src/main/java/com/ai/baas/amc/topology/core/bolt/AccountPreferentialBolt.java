package com.ai.baas.amc.topology.core.bolt;

import java.util.Arrays;
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

import com.ai.baas.amc.topology.core.message.AMCMessageParser;
import com.ai.baas.dshm.client.CacheFactoryUtil;
import com.ai.baas.dshm.client.impl.CacheBLMapper;
import com.ai.baas.dshm.client.impl.DshmClient;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.message.RecordFmt;
import com.ai.baas.storm.util.BaseConstants;

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

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        LOG.info("账务优惠bolt[prepare方法]...");
        /* 1.初始化JDBC */
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        /* 2.获取报文格式信息 */
        mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT,
                BaseConstants.JDBC_DEFAULT);

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
            /* 2.根据传入的详单科目查询对应的账单科目 */
            
            /* 3.累账，将记录中的数据，增加到对应账单中(记录到内存中，不沉淀到数据库) */
            
            /* 4.优惠计算 */
            /* 4.1 根据产品信息，查询该账户订购的产品列表 */

            /* 4.2 循环遍历产品列表，根据优先级，查询对应的扩展信息 */
            
            /* 4.3 根据扩展信息中相应的参数配置，计算优惠额度，对账单项进行优惠 */
            
            /* 5.将计算后结果输出到账单表 */
            /* 6.发送信控消息 */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(outputFields));
    }

    /**
     * 根据传入的详单科目查询对应的账单科目
     * 
     * @param drSubject
     * @return
     * @author LiangMeng
     */
    private String queryBillSubject(String drSubject) {
        return null;
       
    }

    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    private String saveBill() {
        return null;
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
