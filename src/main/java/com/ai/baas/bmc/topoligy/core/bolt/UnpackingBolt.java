package com.ai.baas.bmc.topoligy.core.bolt;

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

import com.ai.baas.bmc.topoligy.core.util.BmcConstants;

public class UnpackingBolt extends BaseBasicBolt {
	private static final long serialVersionUID = -8200039989835637219L;
	private static Logger logger = LoggerFactory.getLogger(UnpackingBolt.class);
	
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
		System.out.println("===============UnpackingBolt====");
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String line = input.getString(0);
		System.out.println("=============line=="+line);
		List<Object> values = null;
		String[] inputDatas = StringUtils.splitPreserveAllTokens(line, BmcConstants.RECORD_SPLIT);
		for(String inputData:inputDatas){
			System.out.println("-------------"+inputData);
			//values.add(inputDatas[i]);
			values = new ArrayList<Object>();
			values.add(inputData);
			collector.emit(values);
		}
		
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(new String[]{"line"}));
	}
	
//	@Override
//	protected void init(Map conf, TopologyContext context) {
//		System.out.println("===============UnpackingBolt====");
//		
//		
//	}
//	
//	@Override
//	protected String[] setOutFields() {
//		return new String[]{"tenant_id","service_id","source","bsn","sn","data"};
//	}
//	
//	@Override
//	protected void work(Tuple input) {
//		// TODO Auto-generated method stub
//		String line = input.getString(0);
//		System.out.println("=============line=="+line);
//		List<Object> values = new ArrayList<Object>();
//		String[] inputDatas = StringUtils.splitPreserveAllTokens(line, BmcConstants.FIELD_SPLIT);
//		for(int i=0;i<6;i++){
//			System.out.println("-------------"+inputDatas[i]);
//			values.add(inputDatas[i]);
//		}
//		//增加处理逻辑
//		super.send(input, values);
//	}
	
	

}
