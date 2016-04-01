package com.ai.baas.amc.topology.core.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.bean.AmcChargeBean;
import com.ai.baas.storm.jdbc.JdbcTemplate;

/**
 * 账单明细dao
 * Date: 2016年3月31日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AmcChargeDAO {
    private static Logger LOG = LoggerFactory.getLogger(AmcChargeDAO.class);
    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    public void saveAmcChargeBean(AmcChargeBean amcChargeBean,Connection conn) {
             
        QueryRunner runner = new QueryRunner();
        try {
            conn.setAutoCommit(false);
            StringBuffer sqlCharge = new StringBuffer();
            int result  = runner.update(conn, sqlCharge.toString());

            StringBuffer sqlInvoice = new StringBuffer();
            int invoice  = runner.update(conn, sqlInvoice.toString());
            conn.commit();
        } catch (Exception e) { 
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("更新账单异常：["+e1.getMessage()+"]",e1);
            }
        }
    }
}
