package com.ai.baas.amc.test;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseClient {

	private static Logger logger = LoggerFactory.getLogger(HbaseClient.class);
	public static final String FIELD_SPLIT = new String(new char[] { (char) 1 });
//	public void run(){
//		Connection conn = HBaseProxy.getConnection();
//		String rowkey = String.valueOf(System.currentTimeMillis());
//		try {
//			Table table = conn.getTable(TableName.valueOf("RTM_OUTPUT_DETAIL_B"));
//			Put put = new Put(rowkey.getBytes());
//			put.addColumn("detail".getBytes(), "psn".getBytes(), "psn11".getBytes());
//			put.addColumn("detail".getBytes(), "sn".getBytes(), "sn33".getBytes());
//			put.addColumn("detail".getBytes(), "line_value".getBytes(), "123456abcd".getBytes());
//			
//			table.put(put);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("", e);
//		} finally{
//			try {
//				conn.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				logger.error("", e);
//			}
//		}
//	}
	
	public void run(){
		Connection conn = HBaseProxy.getConnection();
		StringBuffer str = new StringBuffer();
		str.append("91919191");
		//String rowkey = String.valueOf(str.toString());
		try {
			String rowkey = str.toString();
			System.out.println("rowkey============================="+rowkey);
			logger.debug("rowkey============================="+rowkey);
			Table table = conn.getTable(TableName.valueOf("VIV-BYD_GPRS_SXYD_201512"));
			Get get = new Get(rowkey.getBytes());
			Result result = table.get(get);
//			byte[] subs_id = result.getValue("subs_id".getBytes(), "subs_id".getBytes());
//			if(subs_id == null){
//				System.out.println("------------------------------");
//				logger.debug("------------------------------");
//			}else{
//				System.out.println("subs_id="+new String(subs_id));
//			}
			
			Scan scan = new Scan();
			Filter filter = new RowFilter(CompareOp.EQUAL, new RegexStringComparator(rowkey));
			scan.setFilter(filter);
	        ResultScanner rs = table.getScanner(scan);
	        System.out.println("rs============================="+rs);
	        for (Result rt : rs) {
	        	byte[] subs_id = rt.getValue("subs_id".getBytes(), "subs_id".getBytes());
	        	if(subs_id == null){
					System.out.println("------------------------------");
					logger.debug("------------------------------");
				}else{
					System.out.println("subs_id="+new String(subs_id));
				}
	        }
			
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("", e);
		} finally{
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("", e);
			}
		}
	}
	
	public static void main(String[] args) {
		HbaseClient client = new HbaseClient();
		client.run();
	}

}
