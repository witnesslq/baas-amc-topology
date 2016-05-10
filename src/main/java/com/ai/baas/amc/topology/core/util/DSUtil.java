package com.ai.baas.amc.topology.core.util;

import java.util.Map;

import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.sdk.components.sequence.datasource.SeqDataSourceLoader;
import com.ai.opt.sdk.components.sequence.datasource.SeqDataSourceLoaderFactory;
import com.alibaba.fastjson.JSONObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DSUtil {
    public static void initSeqDS(Map<String,String> confMap){
        String jsonJdbcParam = confMap.get(BaseConstants.JDBC_DEFAULT);
        JSONObject json = JSONObject.parseObject(jsonJdbcParam);        
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(json.getString("jdbc.driver"));
        config.setJdbcUrl(json.getString("jdbc.url"));
        config.setUsername(json.getString("jdbc.username"));
        config.setPassword(json.getString("jdbc.password"));
        config.setAutoCommit(true);
        config.setConnectionTimeout(30000);
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(1800000);
        config.setIdleTimeout(600000);
        
        HikariDataSource ds = new HikariDataSource(config);
        SeqDataSourceLoader loader = new SeqDataSourceLoader();
        loader.setDs(ds);
        loader.init();
        SeqDataSourceLoaderFactory.init(loader);
    }
}
