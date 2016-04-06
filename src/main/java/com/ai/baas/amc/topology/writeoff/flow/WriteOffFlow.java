package com.ai.baas.amc.topology.writeoff.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.flow.BaseFlow;

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
        
	}

	public static void main(String[] args) {
        LOG.info("账务优惠拓扑开始运行...");
		WriteOffFlow flow = new WriteOffFlow();
		flow.run(args);
	}

	
}
