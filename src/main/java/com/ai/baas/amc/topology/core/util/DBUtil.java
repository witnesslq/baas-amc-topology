package com.ai.baas.amc.topology.core.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBUtil {
    private static Logger LOG = LoggerFactory.getLogger(DBUtil.class);
    
    /**
     * 单条更新
     * @param sql
     * @param conn
     * @author LiangMeng
     */
    public static int saveOrUpdate(String sql,Connection conn,boolean commit)  throws Exception{
        QueryRunner runner = new QueryRunner();
        int result = 0 ;
        try {
            result = runner.update(conn, sql.toString());
            if(commit){
            conn.commit();
            }
        } catch (Exception e) { 
            LOG.error("单条更新异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("单条更新异常：["+e1.getMessage()+"]",e1);
            }
            throw e;
        }
        return result;
    }
    
    
    /**
     * 批量更新
     * @param sqls
     * @param conn
     * @author LiangMeng
     */
    public static boolean saveOrUpdateBatch(String[] sqls,Connection conn) throws Exception{
        QueryRunner runner = new QueryRunner();
        boolean isSuccess = true;
        try {
            conn.setAutoCommit(false);
            for(String sql :sqls){
                int result = runner.update(conn, sql.toString());
                if(result<=0){
                  isSuccess = false;  
                }
            }
            if(isSuccess){
                conn.commit();
            }else{
                conn.rollback();
                LOG.error("批量更新异常：[有数据未更新]");
            }
        } catch (Exception e) { 
            LOG.error("批量更新异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("批量更新异常：["+e1.getMessage()+"]",e1);
            }
            throw e;
        }
        return isSuccess;
    }
}
