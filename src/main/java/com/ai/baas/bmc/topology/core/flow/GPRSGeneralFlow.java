package com.ai.baas.bmc.topology.core.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.bmc.topology.core.bolt.DuplicateCheckingBolt;
import com.ai.baas.bmc.topology.core.bolt.UnpackingBolt;
import com.ai.baas.storm.flow.BaseFlow;
import com.ai.baas.storm.util.BaseConstants;

/**
 * GPRS通用拓扑图
 * @author majun
 * @since 2016.3.16
 */
public class GPRSGeneralFlow extends BaseFlow {
	private static Logger logger = LoggerFactory.getLogger(GPRSGeneralFlow.class);
	
	@Override
	public void define() {
		super.setKafkaSpout();
		builder.setBolt("unpacking", new UnpackingBolt(), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
		builder.setBolt("duplicate-checking", new DuplicateCheckingBolt(), 1).shuffleGrouping("unpacking");
	}

	public static void main(String[] args) {
		GPRSGeneralFlow flow = new GPRSGeneralFlow();
		flow.run(args);
	}

}
