package com.ai.baas.amc.topology.core.bolt;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
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
    
    
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	    LOG.info("查重bolt[prepare方法]...");
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
	    LOG.info("查重bolt[execute方法]...");
	    /*1.接收输入报文*/
	    String inputData = input.getString(0);
	    LOG.info("查重bolt输入消息报文：["+inputData+"]...");
	    /*2.解析报文*/
	    
	    /*3.查重*/
	        /*3.1 如果重复，则进错单*/ 
	        /*3.2 如果不重复，则进备份*/
	    /*5.将报文输出到下一环节*/
        List<Object> datas = new ArrayList<Object>();
	    datas.add(inputData);
	    collector.emit(datas);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields(outputFields));
	}
	

}
