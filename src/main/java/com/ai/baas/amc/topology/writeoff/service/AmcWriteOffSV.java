package com.ai.baas.amc.topology.writeoff.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.DBUtil;
import com.ai.baas.amc.topology.core.util.DateUtil;
import com.ai.baas.amc.topology.preferential.bean.AmcChargeBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcDeductRuleBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcFundBookBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcOweInfoBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcSettleDetailBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcSettleLogBean;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.opt.base.exception.SystemException;

/**
 * 销账SV
 * Date: 2016年3月31日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AmcWriteOffSV implements Serializable{
    private static final long serialVersionUID = -3092147670752739621L;
    private static Logger LOG = LoggerFactory.getLogger(AmcWriteOffSV.class);
  
    /**
     * 销账核心服务
     * @param acctId
     * @param conn
     * @author LiangMeng
     */
    public boolean writeOffCore(String acctId,String tenantId,Connection conn){
        boolean isSuccess=true;
        try {
            conn.setAutoCommit(false); 
            /*1.查询账本列表*/
            List<AmcFundBookBean> fundBookList = this.queryFundBookList(tenantId,acctId, conn);
            /*2.查询可销账月份*/
            List<Map<String,Object>> writeOffMonthList = this.queryWriteOffMonths(tenantId,acctId, conn);
            
            String thisMonth = new SimpleDateFormat("yyyyMM").format(new Date());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            /*3.根据不同月份，处理销账*/
            for(Map<String,Object> map : writeOffMonthList){
                String billMonth = (String)map.get("yyyyMM");
                /*3.1 查询账单列表*/
                List<AmcChargeBean> chargeList = this.queryChargeList(tenantId,acctId, billMonth,conn);
                for(AmcChargeBean chargeBean : chargeList){
                    long chargeBalance = chargeBean.getBalance();
                    String feeSubject = chargeBean.getSubjectId()+"";
                    /*3.2 根据账单中的科目，查询可销账资金科目*/
                    String fundSubjectId = this.queryFundSubject(tenantId, feeSubject, conn);
                    /*3.3 根据资金科目获取账本中的账本余额*/
                    AmcFundBookBean thisFundBook = null;
                    for(AmcFundBookBean amcFundBookBean : fundBookList){
                        if(fundSubjectId.equals(amcFundBookBean.getSubjectId()+"")){
                            thisFundBook = amcFundBookBean;
                        }
                    }
                    long fundBookBalance = thisFundBook.getBalance();
                    long bookId = thisFundBook.getBookId();
                    long settleTotal = 0;
                    /*3.4 判断余额是否充足*/
                    if(fundBookBalance==0||chargeBalance==0){
                        /*3.4.1 账本余额为0，则什么都不处理*/
                        continue;
                    }else if(fundBookBalance>=chargeBalance){
                        /*3.4.3 如果账本中月充足，则销掉该账单的金额，将账本金额为（账本金额-账单金额），账单未销账金额更新为0 */
                        int resultFundBook = this.updataFundBookBalance(tenantId, bookId, chargeBalance, conn);
                        settleTotal = chargeBalance;
                        int resultCharge = this.updataChargeBalance(tenantId,billMonth,feeSubject, acctId, settleTotal, conn);    
                        if(resultFundBook == 0||resultCharge == 0){
                            isSuccess = false;
                        }
                    }else{
                        /*3.4.4 如果账本中月不充足，则销掉该账本的金额，将账本更新为0，账单未销账金额为（账单金额-账本金额）*/
                        int resultFundBook = this.updataFundBookBalance(tenantId, bookId, fundBookBalance, conn);
                        settleTotal = fundBookBalance;
                        int resultCharge = this.updataChargeBalance(tenantId,billMonth,feeSubject, acctId, settleTotal, conn);    
                        if(resultFundBook == 0||resultCharge == 0){
                            isSuccess = false;
                        }
                    }
                    /*4.记录销账明细*/
                    AmcSettleDetailBean amcSettleDetailBean = new AmcSettleDetailBean();
                    amcSettleDetailBean.setAcctId(chargeBean.getAcctId());
                    amcSettleDetailBean.setBookId(thisFundBook.getBookId());
                    amcSettleDetailBean.setBusiOperCode("");
                    amcSettleDetailBean.setChargeSeq(chargeBean.getChargeSeq());
                    amcSettleDetailBean.setCreateTime(now);
                    amcSettleDetailBean.setCycleMonth(billMonth);
                    amcSettleDetailBean.setFeeSubjectId(Long.parseLong(feeSubject));
                    amcSettleDetailBean.setFundSubjectId(Long.parseLong(fundSubjectId));
                    amcSettleDetailBean.setInvoiceSeq(0);
                    amcSettleDetailBean.setLastStatusDate(now);
                    amcSettleDetailBean.setSettleType(0);
                    amcSettleDetailBean.setSettlOrder(0);
                    amcSettleDetailBean.setSerialcode("");
                    amcSettleDetailBean.setSubsId(chargeBean.getSubsId());
                    amcSettleDetailBean.setSvcType(0);
                    amcSettleDetailBean.setTenantId(tenantId);
                    amcSettleDetailBean.setTotal(settleTotal);
                    int resultSettleDetail = this.saveSettleDetailBean(amcSettleDetailBean,thisMonth,conn);
                    if(resultSettleDetail == 0){
                        isSuccess = false;
                    }
                }
            }
            /*5.记录销账流水*/
            AmcSettleLogBean amcSettleLogBean = new AmcSettleLogBean();
            int resultSettleLog = this.saveSettleLogBean(amcSettleLogBean,thisMonth,conn);
            if(resultSettleLog == 0){
                isSuccess = false;
            }
            if(isSuccess){
                conn.commit();
            }else{
                conn.rollback();
            }
        } catch (Exception e) { 
            LOG.error("销账异常：["+e.getMessage()+"]",e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("销账异常：["+e1.getMessage()+"]",e1);
            }
        }
        return isSuccess;
    }
    
    /**
     * 查询账本列表
     * @return
     * @author LiangMeng
     */
    private List<AmcFundBookBean> queryFundBookList(String tenantId,String acctId,Connection conn){
        LOG.info("查询账本列表...");
        List<AmcFundBookBean> list = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select tenant_id as tenantId,cust_id as custId,acct_id as acctId,book_id as bookId,subject_type,subjectType,");
        sql.append("subject_id as subjectId,balance as balance,feature_code as featureCode,book_status as bookStatus,");
        sql.append("effect_date as effectDate,expire_date as expireDate,create_time as createTime,subs_freeze_id as subsFreezeId,subs_id as subsId ");
        sql.append("from fun_fund_book where acct_id = ");
        sql.append(acctId);
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        try {
            if (conn != null){
                list = JdbcTemplate.query(sql.toString(),conn,  new BeanListHandler<AmcFundBookBean>(AmcFundBookBean.class));
            }else{
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错",e);
        }
        return list;
    }
    /**
     * 获取未销账月份列表
     * @param acctId
     * @return
     * @author LiangMeng
     */
    private List<Map<String,Object>> queryWriteOffMonths(String tenantId,String acctId,Connection conn){
        List<Map<String,Object>> list = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select tenant_id as tenantId,acct_id as acctId,");
        sql.append("balance,month,create_time as createTime,confirm_time as confirmTime ");
        sql.append("from amc_owe_info where acct_id = ");
        sql.append(acctId);
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        AmcOweInfoBean amcOweInfoBean = null;
        try {
            if (conn != null){
                List<AmcOweInfoBean> result = JdbcTemplate.query(sql.toString(),conn,  new BeanListHandler<AmcOweInfoBean>(AmcOweInfoBean.class));
                if(result!=null&&result.size()>0){
                    amcOweInfoBean = result.get(0);
                    String month = amcOweInfoBean.getMonth();                     
                    if(month==null){
                        throw new SystemException("获取最后未销账月份出错");
                    }else{
                       list = DateUtil.getPerMonth(month);
                    }
                }
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
    private List<AmcChargeBean> queryChargeList(String tenantId,String acctId,String billMonth,Connection conn){
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq,acct_id,subs_id,service_id,subject_id,total_amount,");
        sql.append("       adjust_afterwards,disc_total_amount,balance,pay_status,");
        sql.append("       last_pay_date,cust_id,cust_type,tenant_id ");
        sql.append(" from amc_charge_"+billMonth+" ");
        sql.append(" where acct_id="+acctId+" ");
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        LOG.info("账单查询语句：["+sql+"]");
        List<AmcChargeBean> list = null;
        try {
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
     * 根据费用科目查询资金科目
     * @param feeSubject
     * @return
     * @author LiangMeng
     */
    private String queryFundSubject(String tenantId,String feeSubject,Connection conn){
        String fundSubject = null;
        String sql = "select a.fund_subject as fundSubject from amc_deduct_rule a  where a.FEE_SUBJECT='"+feeSubject+"' and TENANT_ID='"+tenantId+"'";
        try {
            if (conn != null){
                List<AmcDeductRuleBean> list = JdbcTemplate.query(sql,conn,  new BeanListHandler<AmcDeductRuleBean>(AmcDeductRuleBean.class));
                if(list==null||list.size()==0){
                    throw new SystemException("999999", "根据消费科目["+feeSubject+"],未查询到资金科目");
                }else if(list.size()>0){
                    throw new SystemException("999999", "根据消费科目["+feeSubject+"],查询到多个资金科目");
                }else{
                    AmcDeductRuleBean amcDeductRuleBean = list.get(0);
                    fundSubject = amcDeductRuleBean.getFundAubject();
                }
            }else{
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错",e);
        }
        
        return fundSubject;
    }
    /**
     * 更新账本余额
     * @param tenantId
     * @param acctId
     * @param balance
     * @param conn
     * @return
     * @author LiangMeng
     */
    private int updataFundBookBalance(String tenantId, long bookId, long balance, Connection conn)
            throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update amc_fund_book set balance = balance-");
            sql.append(balance);
            sql.append(" where book_id=");
            sql.append(bookId);
            LOG.info("更新账本余额sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("更新账本余额异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        return result;
    }
    /**
     * 更新账单未销账金额
     * @param tenantId
     * @param acctId
     * @param balance
     * @param conn
     * @return
     * @author LiangMeng
     */
    private int updataChargeBalance(String tenantId,String billMonth,
            String subjectId,String acctId,long balance,Connection conn) throws Exception{
        int result = 0;
        try {
            StringBuffer sqlCharge = new StringBuffer();
            sqlCharge.append("update amc_charge_");
            sqlCharge.append(billMonth);
            sqlCharge.append(" set balance = balance-");
            sqlCharge.append(balance);
            sqlCharge.append(" ,last_pay_date = now() where acct_id=");
            sqlCharge.append(acctId);
            sqlCharge.append(" and subject_id=");
            sqlCharge.append(subjectId);
            LOG.info("更新账单明细未销账金额sql：[" + sqlCharge + "]");
            result = DBUtil.saveOrUpdate(sqlCharge.toString(), conn, false);
            
            StringBuffer sqlInvoice = new StringBuffer();
            sqlInvoice.append("update amc_invoice_");
            sqlInvoice.append(billMonth);
            sqlInvoice.append(" set balance = balance-");
            sqlInvoice.append(balance);
            sqlInvoice.append(" ,last_pay_date = now() where acct_id=");
            sqlInvoice.append(acctId);
            LOG.info("更新账单总表未销账金额sql：[" + sqlInvoice + "]");
            result = DBUtil.saveOrUpdate(sqlInvoice.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("更新账单未销账金额异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        return result;
    }
    /**
     * 新增销账流水
     * @param amcSettleLogBean
     * @return
     * @author LiangMeng
     */
    private int saveSettleLogBean(AmcSettleLogBean amcSettleLogBean,String billMonth,Connection conn) throws Exception{
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_settle_log_201605(serial_code,tenant_id,busi_oper_code,acct_id,");
            sql.append("        settle_mode,settle_type,total,status,last_status_date,cancel_serial_code,create_time)");
            sql.append("        values (");
            sql.append(amcSettleLogBean.getSerialCode());
            sql.append(",'");
            sql.append(amcSettleLogBean.getTenantId());
            sql.append("','");
            sql.append(amcSettleLogBean.getBusiOperCode());
            sql.append("',");
            sql.append(amcSettleLogBean.getAcctId());
            sql.append(",");
            sql.append(amcSettleLogBean.getSettleMode());
            sql.append(",");
            sql.append(amcSettleLogBean.getSettleType());
            sql.append(",");
            sql.append(amcSettleLogBean.getTotal());
            sql.append(",");
            sql.append(amcSettleLogBean.getStatus());
            sql.append(",");
            sql.append("now()");
            sql.append(",");
            sql.append(amcSettleLogBean.getCancelSerialCode());
            sql.append(",");
            sql.append("now()");
            sql.append(")");
            LOG.info("记录[amc_settle_log"+billMonth+"]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账记录异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        
        return result;
    }
    /**
     * 新增销账流水明细
     * @param amcSettleDetailBean
     * @return
     * @author LiangMeng
     */
    private int saveSettleDetailBean(AmcSettleDetailBean amcSettleDetailBean,String billMonth,Connection conn) throws Exception{
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_settle_detail_"+billMonth+"(serial_code,tenant_id,busi_oper_code,acct_id,");
            sql.append("        settle_mode,settle_type,book_id,subs_id,svc_type,fund_subject_id,cycle_month,");
            sql.append("        invoice_seq,charge_seq,fee_subject_id,total,create_time,status,");
            sql.append("        last_status_date,settle_order) ");
            sql.append("        values ('");
            sql.append(amcSettleDetailBean.getSerialcode());
            sql.append("','");
            sql.append(amcSettleDetailBean.getTenantId());
            sql.append("','");
            sql.append(amcSettleDetailBean.getBusiOperCode());
            sql.append("',");
            sql.append(amcSettleDetailBean.getAcctId());
            sql.append(",");
            sql.append(amcSettleDetailBean.getSettleMode());
            sql.append(",");
            sql.append(amcSettleDetailBean.getSettleType());
            sql.append(",");
            sql.append(amcSettleDetailBean.getBookId());
            sql.append(",");
            sql.append(amcSettleDetailBean.getSubsId());
            sql.append(",");
            sql.append(amcSettleDetailBean.getSvcType());
            sql.append(",");
            sql.append(amcSettleDetailBean.getFundSubjectId());
            sql.append(",'");
            sql.append(amcSettleDetailBean.getCycleMonth());
            sql.append("',");
            sql.append(amcSettleDetailBean.getInvoiceSeq());
            sql.append(",");
            sql.append(amcSettleDetailBean.getChargeSeq());
            sql.append(",");
            sql.append(amcSettleDetailBean.getFeeSubjectId());
            sql.append(",");
            sql.append(amcSettleDetailBean.getTotal());
            sql.append(",");
            sql.append("now(),");
            sql.append(amcSettleDetailBean.getStatus());
            sql.append(",");
            sql.append("now()");
            sql.append(",");
            sql.append(amcSettleDetailBean.getSettlOrder());
            sql.append(")");
            LOG.info("记录[amc_settle_detail"+billMonth+"]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账明细异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        
        return result;
    }
}
