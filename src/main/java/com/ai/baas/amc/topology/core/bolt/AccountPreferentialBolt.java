package com.ai.baas.amc.topology.core.bolt;
import java.sql.Connection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
/**
 * 账务优惠bolt
 * Date: 2016年3月23日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AccountPreferentialBolt extends BaseBasicBolt {
	private static final long serialVersionUID = -4549737615575118377L;

    private static Logger LOG = LoggerFactory.getLogger(AccountPreferentialBolt.class);
    private String[] outputFields=new String[]{"data"};
    private Connection connection;
	@Override
	public void prepare(Map stormConf, TopologyContext context) {
	    LOG.info("AccountPreferentialBolt.prepare()");
	    /*1.加载JDBC连接信息*/
//	    JdbcProxy.loadResource(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
//	    try {
//            connection = JdbcProxy.getConnection("");
//        } catch (Exception e) {
//            LOG.info("获取JDBC连接失败");
//            //TODO 抛异常
//        }
	}
	
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		LOG.info("账务优惠任务开始");
	    String out = input.getString(0);
	    LOG.info("----------------out=="+out);
		/*1.获取并解析输入信息*/
		//TODO
		/*2.备份到Hbase*/
		/*3.根据传入的详单科目查询对应的账单科目*/
		/*4.累账，将记录中的数据，增加到对应账单中(记录到内存中，不沉淀到数据库)*/
		/*5.优惠计算*/
		    /*5.1 根据产品信息，查询该账户订购的产品列表*/
		    
		    /*5.2 循环遍历产品列表，根据优先级，查询对应的扩展信息*/
		    /*5.3 根据扩展信息中相应的参数配置，计算优惠额度，对账单项进行优惠*/
		/*6.将计算后结果输出到账单表*/
		/*7.发送信控消息*/
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	    declarer.declare(new Fields(outputFields));
	}
	
	/**
	 * 2.备份到Hbase
	 * @return
	 * @author LiangMeng
	 */
	private String backupToHbase(){
	    return null;
	}
	
	/**
	 * 3.根据传入的详单科目查询对应的账单科目
	 * @param drSubject
	 * @return
	 * @author LiangMeng
	 */
	private String queryBillSubject(String drSubject){
	    return null;
	}
	/**
	 * 6.将计算后结果输出到账单表
	 * @return
	 * @author LiangMeng
	 */
	private String saveBill(){
	    return null;
	}
	/**
	 * 7.发送信控消息
	 * @return
	 * @author LiangMeng
	 */
	private String sendXkMsg(){
	    return null;
	}

}
