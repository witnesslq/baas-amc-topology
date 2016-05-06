package com.ai.baas.amc.topology.preferential.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.core.util.AmcUtil;
import com.ai.baas.amc.topology.core.util.DBUtil;
import com.ai.baas.amc.topology.preferential.bean.AmcChargeBean;
import com.ai.baas.amc.topology.preferential.bean.AmcInvoiceBean;
import com.ai.baas.storm.exception.BusinessException;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.util.BaseConstants;
import com.ai.opt.base.exception.SystemException;
import com.ai.opt.sdk.sequence.util.SeqUtil;

/**
 * 账务优惠SV
 * Date: 2016年3月31日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AmcPreferentialSV implements Serializable{
    private static final long serialVersionUID = 7585528077764269928L;
    private static Logger LOG = LoggerFactory.getLogger(AmcPreferentialSV.class);

    /**
     * 查询当前账单
     * @param data
     * @return
     * @author LiangMeng
     */
    public List<AmcChargeBean> queryChargeList( Map<String, String> data,String billMonth){
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq,acct_id,subs_id,service_id,subject_id,total_amount,");
        sql.append("       adjust_afterwards,disc_total_amount,balance,pay_status,");
        sql.append("       last_pay_date,cust_id,cust_type,tenant_id ");
        sql.append(" from amc_charge_"+billMonth+" ");
        sql.append(" where subs_id="+data.get(AmcConstants.FmtFeildName.SUBS_ID)+" ");
        LOG.info("账单查询语句：["+sql+"]");
        List<AmcChargeBean> list = null;
        
        Connection conn = null;
        try {
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            if (conn != null){
                list = JdbcTemplate.query(sql.toString(),conn,  new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
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
    public AmcChargeBean queryCharge(AmcChargeBean bean, Connection conn,String billMonth){
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq as chargeSeq,acct_id as acctId,subs_id as subsId,service_id as serviceId,subject_id as subjectId,total_amount as totalAmount,");
        sql.append("       adjust_afterwards as adjustAfterwards,disc_total_amount as discTotalAmount,balance,pay_status as payStatus,");
        sql.append("       last_pay_date as lastPayDate,cust_id as custId,cust_type as custType,tenant_id as tenantId ");
        sql.append(" from amc_charge_"+billMonth+" ");
        sql.append(" where subs_id="+bean.getSubsId()+" and acct_id="+bean.getAcctId()+" and cust_id="+bean.getCustId()+" and subject_id="+bean.getSubjectId()+" ");
        LOG.info("账单查询语句：["+sql+"]");
        List<AmcChargeBean> list = null;
        
        AmcChargeBean returnBean = null;
        try {
            if (conn != null){
                list = JdbcTemplate.query(sql.toString(),conn,  new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
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
    public boolean  saveOrUpdateAmcChargeBean(AmcChargeBean amcChargeBean,Connection conn,String billMonth) {
        boolean isSuccess=true;
        try {
            conn.setAutoCommit(false);        
            /*1.查询是否存在*/
            AmcChargeBean chargeBeanDB = this.queryCharge(amcChargeBean, conn,billMonth);
            int saveOrUpdateResult  =0;
            if(chargeBeanDB==null||chargeBeanDB.getAcctId()==null){
                /*2.如果不存在该科目账单，则新增*/
                StringBuffer sqlCharge = new StringBuffer();           
                sqlCharge.append(" insert into amc_charge_");
                sqlCharge.append(billMonth);
                sqlCharge.append(" (charge_seq,acct_id,subs_id,service_id,subject_id,total_amount, ");
                sqlCharge.append("         adjust_afterwards,disc_total_amount,balance,pay_status, ");
                sqlCharge.append("         last_pay_date,cust_id,cust_type,tenant_id ) ");
                sqlCharge.append("         values(");
                sqlCharge.append(amcChargeBean.getChargeSeq());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getAcctId());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getSubsId());
                sqlCharge.append(",'");
                sqlCharge.append(amcChargeBean.getServiceId());
                sqlCharge.append("','");
                sqlCharge.append(amcChargeBean.getSubjectId());
                sqlCharge.append("',");
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
                sqlCharge.append("now()");
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getCustId());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getCustType());
                sqlCharge.append(",'");
                sqlCharge.append(amcChargeBean.getTenantId());
                sqlCharge.append("') ");                
                LOG.info("账单插入sql：["+sqlCharge+"]");
                saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlCharge.toString(), conn,false);
            }else{
                /*3.如果已经存在该科目账单，则更新*/
                StringBuffer sqlUpdate = new StringBuffer();
                sqlUpdate.append(" update amc_charge_"+billMonth+" set total_amount = (total_amount+"+amcChargeBean.getTotalAmount()+") ,balance = (balance+"+amcChargeBean.getBalance()+") ,disc_total_amount = (disc_total_amount+"+amcChargeBean.getDiscTotalAmount()+") ");
                sqlUpdate.append(" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                        " and cust_id="+amcChargeBean.getCustId()+" and subject_id="+amcChargeBean.getSubjectId()+" ");
                LOG.info("账单更新语句：["+sqlUpdate+"]");
                saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlUpdate.toString(), conn,false);
            }
            
            int resultInvoice =  this.reBalanceInvoice(amcChargeBean,conn,billMonth);
            if(saveOrUpdateResult>0&&resultInvoice>0){
                conn.commit();
            }else{
                isSuccess = false;
                conn.rollback();
            }
        } catch (Exception e) { 
            LOG.error("更新账单异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("更新账单异常：["+e1.getMessage()+"]",e1);
            }
        }
        return isSuccess;
    }
    
    
    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    public boolean  saveOrUpdateBdBean(long bdAmount,AmcChargeBean amcChargeBean,Connection conn,String billMonth,List<Map<String, String>> billSubjectList) {
        boolean isSuccess=true;
        try {
            conn.setAutoCommit(false);        
            /*1.查询是否存在*/
            String billSubjectCondition = "";
            if(billSubjectList!=null&&billSubjectList.size()>1){
                billSubjectCondition += " and subject_id in(";
                for(int i=0;i<billSubjectList.size();i++){
                    billSubjectCondition+=billSubjectList.get(i).get("ext_value");
                    if(i!=(billSubjectList.size()-1)){
                        billSubjectCondition+=",";
                    }
                }
                billSubjectCondition+= ")";
            }
            long balance = bdAmount;
            long totalAmount = bdAmount;
            String chargeSql = "select  IFNULL(sum(balance),0) as balance ,IFNULL(sum(total_amount),0) as totalAmount from amc_charge_"
            +billMonth+" where acct_id="+amcChargeBean.getAcctId()+" and tenant_id = '"+amcChargeBean.getTenantId()+"'"+
            billSubjectCondition;
            List<AmcChargeBean> list = JdbcTemplate.query(chargeSql.toString(),conn,  new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
            if(list!=null&&list.size()>0){
                balance = balance -list.get(0).getBalance();
                totalAmount = totalAmount -list.get(0).getTotalAmount();
            }
            if(balance<0){
                balance = 0;
            }
            if(totalAmount<0){
                totalAmount = 0;
            }
            
            
            AmcChargeBean chargeBeanDB = this.queryCharge(amcChargeBean, conn,billMonth);
            int saveOrUpdateResult  =0;
            if(chargeBeanDB==null||chargeBeanDB.getAcctId()==null){
                /*2.如果不存在该科目账单，则新增*/
                StringBuffer sqlCharge = new StringBuffer();           
                sqlCharge.append(" insert into amc_charge_");
                sqlCharge.append(billMonth);
                sqlCharge.append(" (charge_seq,acct_id,subs_id,service_id,subject_id,total_amount, ");
                sqlCharge.append("         adjust_afterwards,disc_total_amount,balance,pay_status, ");
                sqlCharge.append("         last_pay_date,cust_id,cust_type,tenant_id ) ");
                sqlCharge.append("         values(");
                sqlCharge.append(amcChargeBean.getChargeSeq());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getAcctId());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getSubsId());
                sqlCharge.append(",'");
                sqlCharge.append(amcChargeBean.getServiceId());
                sqlCharge.append("','");
                sqlCharge.append(amcChargeBean.getSubjectId());
                sqlCharge.append("',");
                sqlCharge.append(totalAmount);
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getAdjustAfterwards());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getDiscTotalAmount());
                sqlCharge.append(",");
                sqlCharge.append(balance);
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getPayStatus());
                sqlCharge.append(",");
                sqlCharge.append("now()");
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getCustId());
                sqlCharge.append(",");
                sqlCharge.append(amcChargeBean.getCustType());
                sqlCharge.append(",'");
                sqlCharge.append(amcChargeBean.getTenantId());
                sqlCharge.append("') ");                
                LOG.info("保底账单科目插入sql：["+sqlCharge+"]");
                saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlCharge.toString(), conn,false);
            }else{
                /*3.如果已经存在该科目账单，则更新*/
                StringBuffer sqlUpdate = new StringBuffer();
                sqlUpdate.append(" update amc_charge_"+billMonth+" set total_amount = "+totalAmount+" ,balance = "+balance+" ");
                sqlUpdate.append(" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                        " and cust_id="+amcChargeBean.getCustId()+" and subject_id="+amcChargeBean.getSubjectId()+" ");
                LOG.info("保底账单科目更新语句：["+sqlUpdate+"]");
                saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlUpdate.toString(), conn,false);
            }

            int resultInvoice =  this.reBalanceInvoice(amcChargeBean,conn,billMonth);
            if(saveOrUpdateResult>0&&resultInvoice>0){
                conn.commit();
            }else{
                isSuccess = false;
                conn.rollback();
            }
        } catch (Exception e) { 
            LOG.error("更新账单异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("更新账单异常：["+e1.getMessage()+"]",e1);
            }
        }
        return isSuccess;
    }
    /**
     * 将计算后结果输出到账单表
     * 
     * @return
     * @author LiangMeng
     */
    public boolean  saveOrUpdateFdBean(long fdAmount,AmcChargeBean amcChargeBean,Connection conn,String billMonth,List<Map<String, String>> billSubjectList) {
        boolean isSuccess=true;
        try {
            conn.setAutoCommit(false);        
            /*1.查询是否存在*/

            String billSubjectCondition = "";
            if(billSubjectList!=null&&billSubjectList.size()>1){
                billSubjectCondition += " and subject_id in(";
                for(int i=0;i<billSubjectList.size();i++){
                    billSubjectCondition+=billSubjectList.get(i).get("ext_value");
                    if(i!=(billSubjectList.size()-1)){
                        billSubjectCondition+=",";
                    }
                }
                billSubjectCondition+= ")";
            }
            AmcChargeBean chargeBeanDB = this.queryCharge(amcChargeBean, conn,billMonth);
            int saveOrUpdateResult  =0;
            long balance = 0;
            long totalAmount = 0;
            String chargeSql = "select  IFNULL(sum(balance),0) as balance ,IFNULL(sum(total_amount),0) as totalAmount from amc_charge_"+billMonth+" where acct_id="+amcChargeBean.getAcctId()+" and tenant_id = '"+amcChargeBean.getTenantId()+"'"+billSubjectCondition;
            List<AmcChargeBean> list = JdbcTemplate.query(chargeSql.toString(),conn,  new BeanListHandler<AmcChargeBean>(AmcChargeBean.class));
            if(list!=null&&list.size()>0){
                balance = list.get(0).getBalance();
                totalAmount = list.get(0).getTotalAmount();
            }
            if(totalAmount>fdAmount){
                
                if(chargeBeanDB==null||chargeBeanDB.getAcctId()==null){
                    /*2.如果不存在该科目账单，则新增*/
                    StringBuffer sqlCharge = new StringBuffer();           
                    sqlCharge.append(" insert into amc_charge_");
                    sqlCharge.append(billMonth);
                    sqlCharge.append(" (charge_seq,acct_id,subs_id,service_id,subject_id,total_amount, ");
                    sqlCharge.append("         adjust_afterwards,disc_total_amount,balance,pay_status, ");
                    sqlCharge.append("         last_pay_date,cust_id,cust_type,tenant_id ) ");
                    sqlCharge.append("         values(");
                    sqlCharge.append(amcChargeBean.getChargeSeq());
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getAcctId());
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getSubsId());
                    sqlCharge.append(",'");
                    sqlCharge.append(amcChargeBean.getServiceId());
                    sqlCharge.append("','");
                    sqlCharge.append(amcChargeBean.getSubjectId());
                    sqlCharge.append("',");
                    sqlCharge.append(fdAmount-totalAmount);
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getAdjustAfterwards());
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getDiscTotalAmount());
                    sqlCharge.append(",");
                    sqlCharge.append(fdAmount-balance);
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getPayStatus());
                    sqlCharge.append(",");
                    sqlCharge.append("now()");
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getCustId());
                    sqlCharge.append(",");
                    sqlCharge.append(amcChargeBean.getCustType());
                    sqlCharge.append(",'");
                    sqlCharge.append(amcChargeBean.getTenantId());
                    sqlCharge.append("') ");                
                    LOG.info("封顶账单科目插入sql：["+sqlCharge+"]");
                    saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlCharge.toString(), conn,false);
                }else{
                    /*3.如果已经存在该科目账单，则更新*/
                    StringBuffer sqlUpdate = new StringBuffer();
                    sqlUpdate.append(" update amc_charge_"+billMonth+" set total_amount = "+(fdAmount-totalAmount)+" ,balance = "+(fdAmount-balance)+" ");
                    sqlUpdate.append(" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                            " and cust_id="+amcChargeBean.getCustId()+" and subject_id="+amcChargeBean.getSubjectId()+" ");
                    LOG.info("封顶账单科目更新语句：["+sqlUpdate+"]");
                    saveOrUpdateResult  = DBUtil.saveOrUpdate(sqlUpdate.toString(), conn,false);
                }
            }
            
            int resultInvoice =  this.reBalanceInvoice(amcChargeBean,conn,billMonth);
            if(saveOrUpdateResult>0&&resultInvoice>0){
                conn.commit();
            }else{
                isSuccess = false;
                conn.rollback();
            }
        } catch (Exception e) { 
            LOG.error("更新账单异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("更新账单异常：["+e1.getMessage()+"]",e1);
            }
        }
        return isSuccess;
    }
    /**
     * 重新平衡invoice表数据
     * @param amcChargeBean
     * @param conn
     * @param billMonth
     * @return
     * @author LiangMeng
     */
    public int reBalanceInvoice(AmcChargeBean amcChargeBean,Connection conn,String billMonth) throws Exception{
        int result = 0;
        AmcInvoiceBean amcInvoiceBean = this.queryInvoice(amcChargeBean, conn, billMonth);
        if(amcInvoiceBean==null||amcInvoiceBean.getAcctId()==0){
           /*1.如果没有账单信息，则更新*/

            String invoiceSeq = SeqUtil.getNewId(
                    AmcConstants.SeqName.AMC_INVOICE$SERIAL_CODE$SEQ, 10);
            amcInvoiceBean = this.initAmcInvoiceBean(amcChargeBean);
            StringBuffer insertSql = new StringBuffer();
            insertSql.append("insert into amc_invoice_");
            insertSql.append(billMonth);
            insertSql.append(" ");
            insertSql.append(" (INVOICE_SEQ,ACCT_ID, ");
            insertSql.append("  ADJUST_AFTERWARDS, ");
            insertSql.append("  BALANCE, ");
            insertSql.append("  CUST_ID, ");
            insertSql.append("  CUST_TYPE, ");
            insertSql.append("  DISC_TOTAL_AMOUNT, ");
            insertSql.append("  LAST_PAY_DATE, ");
            insertSql.append("  PAY_STATUS, ");
            insertSql.append("   PRINT_TIMES, ");
            insertSql.append("  SERVICE_ID, ");
            insertSql.append("  SUBS_ID, ");
            insertSql.append("  TENANT_ID, ");
            insertSql.append("  TOTAL_AMOUNT) ");
            insertSql.append("values ");
            insertSql.append("  ('");
            insertSql.append(invoiceSeq);
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getAcctId());
            insertSql.append("', ");
            insertSql.append("   '");
            insertSql.append(amcInvoiceBean.getAdjustAfterwards());
            insertSql.append("', ");
            insertSql.append("   '");
            insertSql.append(amcInvoiceBean.getBalance());
            insertSql.append("', ");
            insertSql.append("   '");
            insertSql.append(amcInvoiceBean.getCustId());
            insertSql.append("', ");
            insertSql.append(amcInvoiceBean.getCustType());
            insertSql.append(", ");
            insertSql.append("   '");
            insertSql.append(amcInvoiceBean.getDiscTotalAmount());
            insertSql.append("', ");
            insertSql.append("now(),");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getPayStatus());
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getPrintTimes());
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getServiceId());
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getSubsId());
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getTenantId());
            insertSql.append("', ");
            insertSql.append("  '");
            insertSql.append(amcInvoiceBean.getTotalAmount());
            insertSql.append("') ");
            result = DBUtil.saveOrUpdate(insertSql.toString(), conn,false);

            LOG.info("账单总表创建语句：["+insertSql+"]");
        }else{

            StringBuffer sqlUpdate = new StringBuffer();
            sqlUpdate.append(" update amc_invoice_"+billMonth+" set balance = (select sum(balance) from amc_charge_"+billMonth+" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                    " and cust_id="+amcChargeBean.getCustId()+") ,total_amount = (select sum(total_amount) from amc_charge_"+billMonth+" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                    " and cust_id="+amcChargeBean.getCustId()+") ,disc_total_amount = (select sum(disc_total_amount) from amc_charge_"+billMonth+" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                    " and cust_id="+amcChargeBean.getCustId()+") ");
            sqlUpdate.append(" where subs_id="+amcChargeBean.getSubsId()+" and acct_id="+amcChargeBean.getAcctId()+
                    " and cust_id="+amcChargeBean.getCustId()+" ");
            LOG.info("账单总表更新语句：["+sqlUpdate+"]");
            result  = DBUtil.saveOrUpdate(sqlUpdate.toString(), conn,false);
        }
        return result;
    }
    
    public int rebalceOwnInfo(String tenantId,String acctId ) throws Exception{
        /*1.获取最后未销账月份*/
        int result = 0;
        Connection conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
        conn.setAutoCommit(false);
        List<Map<String, Object>> writeOffMonthList = AmcUtil.queryWriteOffMonths(tenantId,
                acctId, conn); 
        long balance = 0;
        if(writeOffMonthList==null||writeOffMonthList.size()==0){
            throw new BusinessException("999999", "获取欠费信息失败，租户id["+tenantId+"]，账户id["+acctId+"]");
        }
        for(Map<String, Object> monthMap : writeOffMonthList){
            String invoiceSql = "select  IFNULL(sum(balance),0) as balance from amc_invoice_"+monthMap.get("yyyyMM")+" where acct_id="+acctId+" and tenant_id = '"+tenantId+"'";
            List<AmcInvoiceBean> list = JdbcTemplate.query(invoiceSql.toString(),conn,  new BeanListHandler<AmcInvoiceBean>(AmcInvoiceBean.class));
            if(list!=null&&list.size()>0){
                balance += list.get(0).getBalance();
            }
           }
        
        StringBuffer sqlUpdate = new StringBuffer();
        sqlUpdate.append(" update amc_owe_info  set balance = ("+balance+") ");
        sqlUpdate.append(" where  acct_id="+acctId+
                "  and tenant_id = '"+tenantId+"'");
        LOG.info("欠费总表更新语句：["+sqlUpdate+"]");
        result  = DBUtil.saveOrUpdate(sqlUpdate.toString(), conn,false);
        conn.commit();
        return result;
    }
   
    
    private AmcInvoiceBean queryInvoice(AmcChargeBean bean,Connection conn,String billMonth){
        AmcInvoiceBean amcInvoiceBean = null;
        StringBuffer sql = new StringBuffer();
        sql.append(" select ACCT_ID as acctId,ADJUST_AFTERWARDS as adjustAfterwards,BALANCE as balance ,CUST_ID as custId,CUST_TYPE as custType,DISC_TOTAL_AMOUNT as discTotalAmount,INVOICE_SEQ as invoiceSeq,LAST_PAY_DATE as lastPayDate,");
        sql.append("PAY_STATUS as payStatus,PRINT_TIMES as printTimes,SERVICE_ID as serviceId,SUBS_ID as subsId,TENANT_ID as tenantId,TOTAL_AMOUNT as totalAmount  ");
        sql.append(" from amc_invoice_"+billMonth+" ");
        sql.append(" where subs_id="+bean.getSubsId()+" and acct_id="+bean.getAcctId()+" and cust_id="+bean.getCustId());
        LOG.info("账单总表查询语句：["+sql+"]");
        List<AmcInvoiceBean> list = null;
        try {
            if (conn != null){
                list = JdbcTemplate.query(sql.toString(),conn,  new BeanListHandler<AmcInvoiceBean>(AmcInvoiceBean.class));
                if(list!=null&&list.size()>0){
                    amcInvoiceBean = list.get(0);
                }
            }else{
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错",e);
        }
        return amcInvoiceBean;
    }
    /**
     * 根据账单详细，初始化账单总表
     * @param amcChargeBean
     * @return
     * @author LiangMeng
     */
    private  AmcInvoiceBean initAmcInvoiceBean(AmcChargeBean amcChargeBean){
        AmcInvoiceBean bean = new AmcInvoiceBean();
        bean.setAcctId(amcChargeBean.getAcctId());
        bean.setAdjustAfterwards(amcChargeBean.getAdjustAfterwards());
        bean.setBalance(amcChargeBean.getBalance());
        bean.setCustId(amcChargeBean.getCustId());
        bean.setCustType(amcChargeBean.getCustType());
        bean.setDiscTotalAmount(amcChargeBean.getDiscTotalAmount());
        bean.setLastPayDate(amcChargeBean.getLastPayDate());
        bean.setPayStatus(amcChargeBean.getPayStatus());
        bean.setPrintTimes(0l);
        bean.setServiceId(amcChargeBean.getServiceId());
        bean.setSubsId(amcChargeBean.getSubsId());
        bean.setTotalAmount(amcChargeBean.getTotalAmount());
        bean.setTenantId(amcChargeBean.getTenantId());
        return bean;
    }
   
}
