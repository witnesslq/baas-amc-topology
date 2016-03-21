package com.ai.baas.amc.topology.core.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.BmcConstants;

public class Records {
	private static Logger logger = LoggerFactory.getLogger(Records.class);
	private String original;
	private Map<String, Integer> indexes;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();
	
	public Records(String original, Map<String, Integer> indexes) {
		this.original = original;
		this.indexes = indexes;
		//System.out.println("index="+indexes);
		String[] recordArr = original.split(BmcConstants.RECORD_SPLIT, -1);
		List<String[]> dataList = new ArrayList<String[]>();
		for (int i = 0; i < recordArr.length; i++) {
			dataList.add(recordArr[i].split(BmcConstants.FIELD_SPLIT, -1));
			data.add(new HashMap<String, String>());
		}
		for (Entry<String, Integer> entry : indexes.entrySet()) {
			for (int i = 0; i < dataList.size(); i++) {
				data.get(i).put(entry.getKey(), dataList.get(i)[entry.getValue()]);
			}
		}
	}
	
	public String getString(int index, String key) {
		return data.get(index).get(key);
	}

	public int getSize() {
		return data.size();
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public Map<String, String> get(int index) {
		return data.get(index);
	}
	
}
