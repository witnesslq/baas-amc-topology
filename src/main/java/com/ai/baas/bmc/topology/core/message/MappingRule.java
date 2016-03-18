package com.ai.baas.bmc.topology.core.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.bmc.topology.core.message.RecordFmt.RecordFmtKey;
import com.ai.baas.storm.jdbc.JdbcTemplate;


public class MappingRule {
	private static Logger logger = LoggerFactory.getLogger(MappingRule.class);
	public static final int FORMAT_TYPE_INPUT = 1;
	public static final int FORMAT_TYPE_OUTPUT = 2;
	private Map<RecordFmtKey, Map<String, Integer>> recordFmtMap = new HashMap<RecordFmtKey, Map<String, Integer>>();
	
	/**
	 * 获取当前类型的映射规则
	 * 
	 * @param formatType
	 * @return
	 */
	public static MappingRule getMappingRule(int formatType,String dbName) {
		logger.debug("load mapping " + formatType);
		MappingRule mappingRule = new MappingRule();
		StringBuilder recordFmtSql = new StringBuilder();
		recordFmtSql.append("select r.tenant_id tenantId,r.service_id serviceId,r.source source");
		recordFmtSql.append("       r.format_type formatType,r.field_serial fieldSerial,r.field_name fieldName,");
		recordFmtSql.append("       r.field_code fieldCode ");
		recordFmtSql.append("from bmc_record_fmt r ");
		recordFmtSql.append("where r.FORMAT_TYPE=").append(formatType);
		List<RecordFmt> recordFmtList = JdbcTemplate.query(recordFmtSql.toString(), dbName,new BeanListHandler<RecordFmt>(RecordFmt.class));
		mappingRule.init(recordFmtList);
		return mappingRule;
	}
	
	public void init(List<RecordFmt> recordFmtList) {
		for (RecordFmt recordFmt : recordFmtList) {
			Map<String, Integer> indexMap = recordFmtMap.get(recordFmt.getRecordFmtKey());
			if (indexMap == null) {
				indexMap = new HashMap<>();
				recordFmtMap.put(recordFmt.getRecordFmtKey(), indexMap);
			}
			indexMap.put(recordFmt.getFieldCode(), recordFmt.getFieldSerial());
		}
		for (Map<String, Integer> map : recordFmtMap.values()) {
			map = sortMap(map);
		}
		System.out.println("there is " + recordFmtMap.size() + " mapping loaded!");
		logger.debug("there is " + recordFmtMap.size() + " mapping loaded!");
	}
	
	private static Map<String, Integer> sortMap(Map<String, Integer> oldMap) {
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(oldMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<java.lang.String, Integer> arg0, Entry<java.lang.String, Integer> arg1) {
				return arg0.getValue() - arg1.getValue();
			}
		});
		Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < list.size(); i++) {
			newMap.put(list.get(i).getKey(), list.get(i).getValue());
		}
		return newMap;
	}
	
	
}
