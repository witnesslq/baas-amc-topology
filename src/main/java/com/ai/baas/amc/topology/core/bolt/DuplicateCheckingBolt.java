package com.ai.baas.amc.topology.core.bolt;

import java.util.ArrayList;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

public class DuplicateCheckingBolt extends BaseBasicBolt {
	private static final long serialVersionUID = -4549737615575118377L;

	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		System.out.println("+++++++++++++++DuplicateCheckingBolt+++++");
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String out = input.getString(0);
		System.out.println("-------------------out=="+out);
		collector.emit(new ArrayList<Object>());
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(new String[]{"tenant_id","service_id","source","bsn","sn","data"}));
	}
	

}
