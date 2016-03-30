package com.ai.baas.amc.topology.core.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.tuple.Fields;

import com.ai.baas.amc.topology.core.bolt.AccountPreferentialBolt;
import com.ai.baas.amc.topology.core.bolt.DuplicateCheckingBolt;
import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.storm.flow.BaseFlow;
import com.ai.baas.storm.util.BaseConstants;

/**
 * 通用拓扑图
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AccountPreferentialFlow extends BaseFlow {
	private static Logger LOG = LoggerFactory.getLogger(AccountPreferentialFlow.class);
	
	@Override
	public void define() {
	    /*1.kafkaspout共用*/
		super.setKafkaSpout();
		/*2.设置查重bolt*/
		builder.setBolt(AmcConstants.BoltName.DUPLICATE_CHECKING_BOLT, new DuplicateCheckingBolt(), 1).shuffleGrouping(BaseConstants.KAFKA_SPOUT_NAME);
		/*3.设置账务优惠bolt*/
		builder.setBolt(AmcConstants.BoltName.ACCOUNT_PREFERENTIAL_BOLT, new AccountPreferentialBolt(), 1).fieldsGrouping(AmcConstants.BoltName.DUPLICATE_CHECKING_BOLT, new Fields(AmcConstants.FmtFeildName.ACCT_ID));
        
	}

	public static void main(String[] args) {
        LOG.info("账务优惠拓扑开始运行...");
		AccountPreferentialFlow flow = new AccountPreferentialFlow();
		flow.run(args);
	}

	
}
