package com.ai.baas.amc.topology.writeoff.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.writeoff.bolt.WriteOffBolt;
import com.ai.baas.storm.flow.BaseFlow;
import com.ai.baas.storm.util.BaseConstants;

/**
 * 销账拓扑
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class WriteOffFlow extends BaseFlow {
	private static Logger LOG = LoggerFactory.getLogger(WriteOffFlow.class);	
	@Override
	public void define() {
	    /*1.kafkaspout共用*/
		super.setKafkaSpout();
		/*3.设置销账bolt*/
		builder.setBolt(AmcConstants.BoltName.WRITE_OFF_BOLT, new WriteOffBolt(), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
        
	}

	public static void main(String[] args) {
        LOG.info("销账拓扑开始运行...");
		WriteOffFlow flow = new WriteOffFlow();
		flow.run(args);
	}

}
