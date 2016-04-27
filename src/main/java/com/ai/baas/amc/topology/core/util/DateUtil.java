package com.ai.baas.amc.topology.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateUtil {
    /**
     * 
     * @param count
     * @return
     * @author LiangMeng
     */
//    public static List<Map<String,Object>> getPerMonth(String monthParam){
//        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//        for(int i=0;i<Integer.MAX_VALUE;i++){
//            Calendar cal = Calendar.getInstance();
//            cal.add(Calendar.MONTH, -i);
//            Map<String,Object> map = new HashMap<String, Object>();
//            int year = cal.get(Calendar.YEAR);
//            int month = (cal.get(Calendar.MONTH)+1);
//            String yyyyMM = String.valueOf(year) + (month<10? "0"+month : month);
//            map.put("formatStr",  year+ "年" + month+"月");
//            map.put("year", year);
//            map.put("month", month);
//            map.put("yyyyMM",yyyyMM );
//            list.add(map);
//            if(monthParam.equals(yyyyMM)){
//              break;  
//            }
//        }
//        return list;
//    }
    
    public static List<Map<String,Object>> getPerMonth(String monthParam) throws Exception{
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        for(int i=0;i<Integer.MAX_VALUE;i++){
            String nowMonth = new SimpleDateFormat("yyyyMM").format(new Date());
            Date date = new SimpleDateFormat("yyyyMM").parse(monthParam);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MONTH, i);
            Map<String,Object> map = new HashMap<String, Object>();
            int year = cal.get(Calendar.YEAR);
            int month = (cal.get(Calendar.MONTH)+1);
            String yyyyMM = String.valueOf(year) + (month<10? "0"+month : month);
            map.put("formatStr",  year+ "年" + month+"月");
            map.put("year", year);
            map.put("month", month);
            map.put("yyyyMM",yyyyMM );
            list.add(map);
            if(nowMonth.equals(yyyyMM)){
              break;  
            }
        }
        return list;
    }
}
