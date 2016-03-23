package com.ai.baas.amc.topology.core.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.bolt.DuplicateCheckingBolt;
import com.ai.baas.storm.flow.BaseFlow;
import com.ai.baas.storm.util.BaseConstants;

/**
 * 通用拓扑图
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class GeneralFlow extends BaseFlow {
	private static Logger LOG = LoggerFactory.getLogger(GeneralFlow.class);
	
	@Override
	public void define() {
	    LOG.info("GeneralFlow.define()");
		super.setKafkaSpout();
//		builder.setBolt("unpacking", new UnpackingBolt(), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
		builder.setBolt("duplicate-checking", new DuplicateCheckingBolt("f1"), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
	}

	public static void main(String[] args) {
		GeneralFlow flow = new GeneralFlow();
		flow.run(args);
	}

	
}
