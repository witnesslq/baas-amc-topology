package com.ai.baas.amc.topology.core.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.storm.message.MappingRule;
import com.ai.baas.storm.message.RecordFmt.RecordFmtKey;
import com.ai.baas.storm.util.BaseConstants;

/**
 * 单条数据解析类
 * 将字符串数据解析成Map格式
 * Date: 2016年3月25日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AMCMessageParser {
	private static Logger LOG = LoggerFactory.getLogger(AMCMessageParser.class);
	private Map<String, String> data = new HashMap<String, String>();
	private MappingRule[] mappingRules;
	private String[] inputDatas;
	private RecordFmtKey recordFmtKey;
	private static String[] headerKeys;
	
	static{
		headerKeys = new String[] { AmcConstants.FmtFeildName.TENANT_ID,
				AmcConstants.FmtFeildName.SERVICE_ID, AmcConstants.FmtFeildName.SOURCE};
	}
	
	private AMCMessageParser(String original, MappingRule[] mappingRules, String[] outputKeys){
		String[] inputParams = StringUtils.splitPreserveAllTokens(original,BaseConstants.FIELD_SPLIT);
		for(int i=0;i<headerKeys.length;i++){
			data.put(headerKeys[i], inputParams[i]);
		}
		int dataBeginPosi = headerKeys.length;
		int len = inputParams.length - dataBeginPosi;
		inputDatas = new String[len];
		System.arraycopy(inputParams, dataBeginPosi, inputDatas, 0, len);
		this.mappingRules = mappingRules;
	}
	/**
	 * 初始化
	 * @throws Exception
	 * @author LiangMeng
	 */
	private void init() throws Exception{
		recordFmtKey = new RecordFmtKey(data.get(AmcConstants.FmtFeildName.TENANT_ID),data.get(AmcConstants.FmtFeildName.SERVICE_ID),data.get(AmcConstants.FmtFeildName.SOURCE));
		Map<String, Integer> inputMappingRule = mappingRules[0].getIndexes(recordFmtKey);
		for (Entry<String, Integer> entry : inputMappingRule.entrySet()) {
			data.put(entry.getKey(), inputDatas[entry.getValue()]);
		}
	}
	/**
	 * 获取解析后的数据
	 * @return
	 * @author LiangMeng
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * 解析报文
	 * @param original
	 * @param mappingRules
	 * @param outputKeys
	 * @return
	 * @throws Exception
	 * @author LiangMeng
	 */
	public static AMCMessageParser parseObject(String original, MappingRule[] mappingRules, String[] outputKeys) throws Exception{
		if(StringUtils.isBlank(original)){
		    LOG.error("输入报文为空！");
			throw new Exception("输入报文为空！");
		}
		AMCMessageParser messageParser = new AMCMessageParser(original,mappingRules,outputKeys);
		messageParser.init();
		return messageParser;
	}


}
