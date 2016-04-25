package com.ai.baas.amc.test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.core.util.DSUtil;
import com.ai.baas.amc.topology.preferential.bean.AmcChargeBean;
import com.ai.baas.amc.topology.preferential.service.AmcPreferentialSV;
import com.ai.baas.amc.topology.writeoff.service.AmcWriteOffSV;
import com.ai.baas.dshm.client.CacheFactoryUtil;
import com.ai.baas.dshm.client.impl.CacheBLMapper;
import com.ai.baas.dshm.client.impl.DshmClient;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.sdk.datasource.OptHikariDataSource;
import com.ai.opt.sdk.sequence.datasource.SeqDataSourceLoader;
import com.ai.opt.sdk.sequence.datasource.SeqDataSourceLoaderFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DAOTest {
    public static void chargeDaoTest(){
        
        /* 1.初始化JDBC */
        Map<String, String> stormConf = new HashMap<String, String>();
        stormConf.put(BaseConstants.JDBC_DEFAULT, "{\"jdbc.driver\":\"com.mysql.jdbc.Driver\",\"jdbc.url\":\"jdbc:mysql://10.1.235.245:31306/dev_baas_amc1?useUnicode=true&characterEncoding=UTF-8\",\"jdbc.username\":\"amcusr01\",\"jdbc.password\":\"amcusr01_123\"}");
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        AmcPreferentialSV dao = new AmcPreferentialSV();
        Map<String,String> data = new HashMap<String,String>();
        data.put(AmcConstants.FmtFeildName.SUBS_ID, "1");
        data.put(AmcConstants.FmtFeildName.START_TIME,"2016030101");
        dao.queryChargeList(data,"201603");
    }
    
    public static void saveTest(){
        Map<String, String> stormConf = new HashMap<String, String>();
        stormConf.put(BaseConstants.JDBC_DEFAULT, "{\"jdbc.driver\":\"com.mysql.jdbc.Driver\",\"jdbc.url\":\"jdbc:mysql://10.1.235.245:31306/dev_baas_amc1?useUnicode=true&characterEncoding=UTF-8\",\"jdbc.username\":\"amcusr01\",\"jdbc.password\":\"amcusr01_123\"}");
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);
        AmcPreferentialSV dao = new AmcPreferentialSV();
        AmcChargeBean amcChargeBean = new AmcChargeBean();
        amcChargeBean.setTenantId("TR");
        amcChargeBean.setAcctId(Long.parseLong("11"));
        amcChargeBean.setBalance(1000l);
        amcChargeBean.setCustId(Long.parseLong("11"));
        amcChargeBean.setDiscTotalAmount(0l);
        amcChargeBean.setAdjustAfterwards(0l);
        amcChargeBean.setPayStatus(1l);
        amcChargeBean.setServiceId("11");
        amcChargeBean.setSubjectId(100001l);
        amcChargeBean.setSubsId(Long.parseLong("11"));
        amcChargeBean.setTotalAmount(1000l);
        try {
            dao.saveOrUpdateAmcChargeBean(amcChargeBean, JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT), "201605");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeOffTest(){
        Map<String, String> stormConf = new HashMap<String, String>();
        stormConf.put(BaseConstants.JDBC_DEFAULT, "{\"jdbc.driver\":\"com.mysql.jdbc.Driver\",\"jdbc.url\":\"jdbc:mysql://10.1.235.245:31306/dev_baas_amc1?useUnicode=true&characterEncoding=UTF-8\",\"jdbc.username\":\"amcusr01\",\"jdbc.password\":\"amcusr01_123\"}");
        JdbcProxy.loadResources(Arrays.asList(BaseConstants.JDBC_DEFAULT), stormConf);

        ICacheClient cacheClient = null;
        IDshmClient client=null;
        if(client==null){
            client=new DshmClient();
        }
        Properties p=new Properties();
        p.setProperty(AmcConstants.CacheConfig.CCS_APPNAME, "aiopt-baas-dshm");
        p.setProperty(AmcConstants.CacheConfig.CCS_ZK_ADDRESS, "10.1.130.84:39181");
        if(cacheClient==null){
            cacheClient =  CacheFactoryUtil.getCacheClient(p,CacheBLMapper.CACHE_BL_CAL_PARAM);
        }
        
        DSUtil.initSeqDS(stormConf);
        AmcWriteOffSV sv = new AmcWriteOffSV();
        try {
            sv.writeOffCore("17", "VIV-BYD", JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT),cacheClient,client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
       // chargeDaoTest();
        //saveTest();
        writeOffTest();
    }
}
