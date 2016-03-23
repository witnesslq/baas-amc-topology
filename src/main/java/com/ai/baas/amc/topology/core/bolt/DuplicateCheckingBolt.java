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
    private String[] outputFields;
    
    public DuplicateCheckingBolt(String aOutputFields){
        LOG.info("初始化输出格式");
        outputFields = StringUtils.splitPreserveAllTokens(aOutputFields, ",");
    }
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	    LOG.info("DuplicateCheckingBolt.prepare()");
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		LOG.info("DuplicateCheckingBolt.execute()");
		List<Object> list = new  ArrayList<Object>();
		list.add("test2");
		list.add("test22");
		list.add("test23");
		list.add("test24");
		collector.emit(list);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields(outputFields));
	}
	

}
