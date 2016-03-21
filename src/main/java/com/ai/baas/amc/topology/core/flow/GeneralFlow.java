package com.ai.baas.amc.topology.core.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.bolt.DuplicateCheckingBolt;
import com.ai.baas.storm.flow.BaseFlow;
import com.ai.baas.storm.util.BaseConstants;

/**
 * GPRS通用拓扑图
 * @author majun
 * @since 2016.3.16
 */
public class GeneralFlow extends BaseFlow {
	private static Logger logger = LoggerFactory.getLogger(GeneralFlow.class);
	
	@Override
	public void define() {
		super.setKafkaSpout();
//		builder.setBolt("unpacking", new UnpackingBolt(), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
		builder.setBolt("duplicate-checking", new DuplicateCheckingBolt(), 1).shuffleGrouping("unpacking");
	}

	public static void main(String[] args) {
		GeneralFlow flow = new GeneralFlow();
		flow.run(args);
	}

	
}
