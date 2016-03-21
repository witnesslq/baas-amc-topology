package com.ai.baas.bmc.topology.core.message;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import backtype.storm.tuple.Tuple;

import com.ai.baas.bmc.topology.core.message.RecordFmt.RecordFmtKey;
import com.ai.baas.bmc.topology.core.util.BmcException;



public class MessageParser {
	
	private String[] orderHeadKeys;
	private Map<String, String> data = new HashMap<String, String>();
	private MappingRule[] mappingRules;//0:inputMappingRule  1:outMappingRule
	private Map<String, Integer> inIndexes;
	private Map<String, Integer> outIndexes;
	private String inputData;
	private RecordFmtKey recordFmtKey;
	
	
	
	private void init() throws BmcException{
		
	}
	

	
	public static MessageParser parseObject(String original, MappingRule[] mappingRules) throws BmcException{
		if(StringUtils.isBlank(original)){
			throw new BmcException("","input String is null!");
		}
		
		MessageParser messageParser = new MessageParser();
		messageParser.init();
		return messageParser;
	}
	
	
	public static MessageParser parseObject(Tuple input, String[] orderHeadKeys, MappingRule[] mappingRules) throws BmcException{
		MessageParser messageParser = new MessageParser();
		messageParser.init();
		return messageParser;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
