package com.ai.baas.amc.topology.core.bolt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.ai.baas.amc.topology.core.message.AMCMessageParser;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.util.BaseConstants;
/**
 * 查重任务
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class DuplicateCheckingBolt extends BaseBasicBolt {
	private static final long serialVersionUID = -4549737615575118377L;

    private static Logger LOG = LoggerFactory.getLogger(DuplicateCheckingBolt.class);
    private String[] outputFields=new String[]{"data"};
    private MappingRule[] mappingRules = new MappingRule[1];
    
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	    LOG.info("查重bolt[prepare方法]...");
	    /*1.初始化JDBC*/
	    JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
	    /*2.获取报文格式信息*/
        mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT, BaseConstants.JDBC_DEFAULT);
        
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
	    LOG.info("查重bolt[execute方法]...");
	    try{
    	    /*1.接收输入报文*/
    	    String inputData = input.getString(0);
    	    LOG.info("查重bolt输入消息报文：["+inputData+"]...");
    	    /*2.解析报文*/
    	    AMCMessageParser messageParser = AMCMessageParser.parseObject(inputData, mappingRules, outputFields);
    	    Map<String,String> data = messageParser.getData();
    	    /*3.查重*/
    	        /*3.1 如果重复，则进错单*/ 
    	        /*3.2 如果不重复，则进备份*/
    	    /*5.将报文输出到下一环节*/
            List<Object> datas = new ArrayList<Object>();
    	    datas.add(inputData);
    	    collector.emit(datas);
	    }catch(Exception e){
            e.printStackTrace();
        }
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields(outputFields));
	}
	

}
