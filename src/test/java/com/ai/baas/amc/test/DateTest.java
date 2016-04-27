package com.ai.baas.amc.test;

import java.util.List;
import java.util.Map;

import com.ai.baas.amc.topology.core.util.DateUtil;

public class DateTest {
    public static void main(String[] args) {
        List<Map<String, Object>> list;
        try {
            list = DateUtil.getPerMonth("201601");
            for(Map<String,Object> map :list){
                System.out.println(map.get("yyyyMM"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
