package com.ai.baas.amc.topology.preferential.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.preferential.bean.AmcChargeBean;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.SystemException;

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
     * 查询当前账单
     * @param data
     * @return
     * @author LiangMeng
     */
    public List<AmcChargeBean> queryChargeList( Map<String, String> data){
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq,acct_id,subs_id,service_id,subject_id,total_amount,");
        sql.append("       adjust_afterwards,disc_total_amount,balance,pay_status,");
        sql.append("       last_pay_date,cust_id,cust_type,tenant_id ");
        sql.append(" from amc_charge_"+data.get(AmcConstants.FmtFeildName.START_TIME).substring(0,6)+" ");
        sql.append(" where subs_id="+data.get(AmcConstants.FmtFeildName.SUBS_ID)+" ");
        LOG.info("账单查询语句：["+sql+"]");
        List<AmcChargeBean> list = null;
        
        Connection conn = null;
        try {
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            if (conn != null){
                QueryRunner runner = new QueryRunner();
                list = runner.query(conn, sql.toString(), new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
                conn.commit();
            }else{
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错",e);
        }
        return list;
    }
    /**
     * 查询当前账单
     * @param data
     * @return
     * @author LiangMeng
     */
    public AmcChargeBean queryCharge(AmcChargeBean bean,String billMonth){
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq,acct_id,subs_id,service_id,subject_id,total_amount,");
        sql.append("       adjust_afterwards,disc_total_amount,balance,pay_status,");
        sql.append("       last_pay_date,cust_id,cust_type,tenant_id ");
        sql.append(" from amc_charge_"+billMonth+" ");
        sql.append(" where subs_id="+bean.getSubsId()+" and acct_id="+bean.getAcctId()+" and cust_id="+bean.getCustId()+" and subject_id="+bean.getSubjectId()+" ");
        LOG.info("账单查询语句：["+sql+"]");
        List<AmcChargeBean> list = null;
        
        Connection conn = null;
        AmcChargeBean returnBean = null;
        try {
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            if (conn != null){
                QueryRunner runner = new QueryRunner();
                list = runner.query(conn, sql.toString(), new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
                if(list!=null&&list.size()>0){
                    returnBean = list.get(0);
                }
            }else{
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错",e);
        }
        return returnBean;
    }
    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    public int saveAmcChargeBean(AmcChargeBean amcChargeBean,Connection conn) {
             int result=0;
        QueryRunner runner = new QueryRunner();
        try {
            conn.setAutoCommit(false);
            StringBuffer sqlCharge = new StringBuffer();           
            sqlCharge.append(" insert into amc_charge_201603(acct_id,subs_id,service_id,subject_id,total_amount, ");
            sqlCharge.append("         adjust_afterwards,disc_total_amount,balance,pay_status, ");
            sqlCharge.append("         last_pay_date,cust_id,cust_type,tenant_id ) ");
            sqlCharge.append("         values(");
            sqlCharge.append(amcChargeBean.getAcctId());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getSubsId());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getServiceId());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getSubjectId());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getTotalAmount());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getAdjustAfterwards());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getDiscTotalAmount());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getBalance());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getPayStatus());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getLastPayDate());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getCustId());
            sqlCharge.append(",");
            sqlCharge.append(amcChargeBean.getCustType());
            sqlCharge.append(",'");
            sqlCharge.append(amcChargeBean.getTenantId());
            sqlCharge.append("') ");
            result  = runner.update(conn, sqlCharge.toString());
        } catch (Exception e) { 
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("更新账单异常：["+e1.getMessage()+"]",e1);
            }
        }
        return result;
    }
    
    public void saveOrUpdateAmcChargeBean(AmcChargeBean amcChargeBean,Connection conn) {
        try {
            conn.setAutoCommit(false);
            
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
