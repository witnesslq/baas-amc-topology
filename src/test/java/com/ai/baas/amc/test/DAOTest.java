package com.ai.baas.amc.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.preferential.dao.AmcChargeDAO;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.util.BaseConstants;

public class DAOTest {
    public static void chargeDaoTest(){
        
        /* 1.初始化JDBC */
        Map<String, String> stormConf = new HashMap<String, String>();
        stormConf.put(BaseConstants.JDBC_DEFAULT, "{\"jdbc.driver\":\"com.mysql.jdbc.Driver\",\"jdbc.url\":\"jdbc:mysql://10.1.235.245:31306/dev_baas_amc1?useUnicode=true&characterEncoding=UTF-8\",\"jdbc.username\":\"amcusr01\",\"jdbc.password\":\"amcusr01_123\"}");
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        AmcChargeDAO dao = new AmcChargeDAO();
        Map<String,String> data = new HashMap<String,String>();
        data.put(AmcConstants.FmtFeildName.SUBS_ID, "1");
        data.put(AmcConstants.FmtFeildName.START_TIME,"2016030101");
        dao.queryChargeList(data);
    }
    public static void main(String[] args) {
        chargeDaoTest();
    }
}
