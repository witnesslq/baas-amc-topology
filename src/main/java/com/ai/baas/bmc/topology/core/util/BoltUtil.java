package com.ai.baas.bmc.topology.core.util;

import java.util.ArrayList;
import java.util.List;



public class BoltUtil {

	/**
	 * 
	 * @param boltName
	 * @return
	 */
	public static String[] getOutputNames(String boltName){
		List<String> outputs = new ArrayList<String>();
		outputs.add(BmcConstants.TENANT_ID);
		outputs.add(BmcConstants.SERVICE_ID);
		outputs.add(BmcConstants.BATCH_SERIAL_NUMBER);
		outputs.add(BmcConstants.SERIAL_NUMBER);
		if(boltName.equals(BmcConstants.RULE_ADAPT_BOLT)){
			outputs.add(BmcConstants.SUBS_ID);
		}
		outputs.add(BmcConstants.RECORD_DATA);
		return outputs.toArray(new String[outputs.size()]);
	}
	
	/**
	 * 
	 * @param boltName
	 * @return
	 */
	public static String[] getInputKeys(String boltName){
		List<String> inputkeys = new ArrayList<String>();
		inputkeys.add(BmcConstants.TENANT_ID);
		inputkeys.add(BmcConstants.SERVICE_ID);
		inputkeys.add(BmcConstants.SOURCE);
		inputkeys.add(BmcConstants.BATCH_SERIAL_NUMBER);
		inputkeys.add(BmcConstants.SERIAL_NUMBER);
		if(boltName.equals(BmcConstants.RULE_ADAPT_BOLT)){
			inputkeys.add(BmcConstants.SUBS_ID);
		}
		return inputkeys.toArray(new String[inputkeys.size()]);
	}
	

}
