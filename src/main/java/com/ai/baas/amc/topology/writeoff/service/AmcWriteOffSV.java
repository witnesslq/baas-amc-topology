package com.ai.baas.amc.topology.writeoff.service;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.amc.topology.core.util.AmcConstants;
import com.ai.baas.amc.topology.core.util.DBUtil;
import com.ai.baas.amc.topology.core.util.DateUtil;
import com.ai.baas.amc.topology.preferential.bean.AmcChargeBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcDeductRuleBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcFundBookBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcFundDetailBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcFundSerialBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcOweInfoBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcSettleDetailBean;
import com.ai.baas.amc.topology.writeoff.bean.AmcSettleLogBean;
import com.ai.baas.dshm.client.interfaces.IDshmClient;
import com.ai.baas.storm.exception.BusinessException;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.opt.base.exception.SystemException;
import com.ai.opt.sdk.sequence.util.SeqUtil;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

/**
 * 销账SV Date: 2016年3月31日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AmcWriteOffSV implements Serializable {
    private static final long serialVersionUID = -3092147670752739621L;

    private static Logger LOG = LoggerFactory.getLogger(AmcWriteOffSV.class);

    /**
     * 销账核心服务
     * 
     * @param acctId
     * @param conn
     * @author LiangMeng
     */
    public boolean writeOffCore(String acctId, String tenantId, Connection conn,
            ICacheClient cacheClient, IDshmClient client) {
        boolean isSuccess = true;
        try {
            conn.setAutoCommit(false);
            /* 1.查询账本列表 */
            List<AmcFundBookBean> fundBookList = this.queryFundBookList(tenantId, acctId, conn);
            /* 2.查询可销账月份 */
            List<Map<String, Object>> writeOffMonthList = this.queryWriteOffMonths(tenantId,
                    acctId, conn);

            String thisMonth = new SimpleDateFormat("yyyyMM").format(new Date());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String settleLogSerial = SeqUtil.getNewId(
                    AmcConstants.SeqName.AMC_SETTLE_LOG$SERIAL_CODE$SEQ, 10);

            long total = 0;
            if(writeOffMonthList==null||writeOffMonthList.size()==0){
                throw new BusinessException("999999", "未获取到最后未销账月份");
            }
            String writeOffMonth  = (String)writeOffMonthList.get(0).get("yyyyMM");
            /* 3.一层寻缘遍历账本列表，先处理专款 */
            for (AmcFundBookBean amcFundBookBean : fundBookList) {
                Timestamp effectDate = amcFundBookBean.getEffectDate();// 失效日期
                Timestamp expireDate = amcFundBookBean.getExpireDate();// 生效日期
                Long subjectId = amcFundBookBean.getSubjectId();
                Long bookId = amcFundBookBean.getBookId();
                long fundBookBalance = amcFundBookBean.getBalance();
                Map<String, String> subjectMap = this.querySubject(tenantId, subjectId + "",
                        cacheClient, client);
                String feeSubjectId = this.queryFeeSubject(tenantId, subjectId + "", conn);
                if (fundBookBalance == 0 ) {
                    continue;
                }
                if ("0".equals(subjectMap.get("can_settle_all"))) {
                    /* 二层循环，遍历可销账月份 */
                    T2: for (Map<String, Object> map : writeOffMonthList) {
                        String billMonth = (String) map.get("yyyyMM");
                        Integer effectMonth = Integer.parseInt(new SimpleDateFormat("yyyyMM")
                                .format(effectDate.getTime()));
                        Integer expireMonth = Integer.parseInt(new SimpleDateFormat("yyyyMM")
                                .format(expireDate.getTime()));
                        Integer billMonthInt = Integer.parseInt(billMonth);
                        /* 账本失效，则不能使用该账本 */
                        if (billMonthInt < effectMonth || effectMonth > expireMonth) {
                            LOG.info("账本[" + bookId + "]不可销["+billMonthInt+"]账期的账");
                            continue T2;
                        } else {
                            LOG.info("账本[" + bookId + "]可销["+billMonthInt+"]账期的账");
                            /* 查询账单列表 */
                            List<AmcChargeBean> chargeList = this.queryChargeList(tenantId, acctId,
                                    billMonth, conn);
                            for (AmcChargeBean chargeBean : chargeList) {
                                long chargeBalance = chargeBean.getBalance();
                                String feeSubject = chargeBean.getSubjectId() + "";
                                if (feeSubject.equals(feeSubjectId)) {
                                    long settleTotal = 0;
                                    if (fundBookBalance == 0 || chargeBalance == 0) {
                                        /* 3.4.1 账本余额为0，则什么都不处理 */
                                        continue;
                                    }
                                    /** 初始化账本变更记录 */
                                    String paySerialCode = SeqUtil
                                            .getNewId(
                                                    AmcConstants.SeqName.AMC_FUND_SERIAL$PAY_SERIAL_CODE$SEQ,
                                                    10);
                                    String fundDetailCode = SeqUtil.getNewId(
                                            AmcConstants.SeqName.AMC_FUND_DETAIL$SERIAL_CODE$SEQ,
                                            10);
                                    AmcFundDetailBean detailBean = new AmcFundDetailBean();
                                    detailBean.setAcctId(acctId);
                                    detailBean.setBalancePre(fundBookBalance);
                                    detailBean.setBookId(bookId);
                                    detailBean.setCreateTime(now);
                                    detailBean.setCustId(amcFundBookBean.getCustId());
                                    detailBean.setOptType("0");
                                    detailBean.setPaySerialCode(paySerialCode);
                                    detailBean.setSubjectId(amcFundBookBean.getSubjectId());
                                    detailBean.setSerialCode(fundDetailCode);

                                    AmcFundSerialBean serialBean = new AmcFundSerialBean();
                                    serialBean.setAcctId1(acctId);
                                    serialBean.setAcctName1("");
                                    serialBean.setAcctId2(null);
                                    serialBean.setAcctName2("");
                                    serialBean.setCustId1(amcFundBookBean.getCustId());
                                    serialBean.setOptType("0");
                                    serialBean.setPayRuleId(0);
                                    serialBean.setCancelSerialCode("0");
                                    serialBean.setPayStatus("0");
                                    serialBean.setTenantId(tenantId);
                                    serialBean.setPaySerialCode(paySerialCode);

                                    /* 3.4 判断余额是否充足 */
                                    if (fundBookBalance >= chargeBalance) {
                                        detailBean.setRemark("销账金额[" + chargeBalance + "]");
                                        detailBean.setTransSummary("销账金额[" + chargeBalance + "]");
                                        detailBean.setTotalAmount(chargeBalance);

                                        serialBean.setRemark("销账金额[" + chargeBalance + "]");
                                        serialBean.setTransSummary("销账金额[" + chargeBalance + "]");
                                        serialBean.setTotalAmount(chargeBalance);
                                        /* 3.4.3 如果账本中月充足，则销掉该账单的金额，将账本金额为（账本金额-账单金额），账单未销账金额更新为0 */
                                        int resultFundBook = this.deductFundBook(detailBean,
                                                serialBean, tenantId, bookId, chargeBalance,
                                                thisMonth, conn);
                                        settleTotal = chargeBalance;
                                        total += chargeBalance;
                                        int resultCharge = this.updataChargeBalance(tenantId,
                                                billMonth, feeSubject, acctId, settleTotal, conn);
                                        if (resultFundBook == 0 || resultCharge == 0) {
                                            isSuccess = false;
                                        }
                                    } else {
                                        detailBean.setRemark("销账金额[" + fundBookBalance + "]");
                                        detailBean.setTransSummary("销账金额[" + fundBookBalance + "]");
                                        detailBean.setTotalAmount(fundBookBalance);

                                        serialBean.setRemark("销账金额[" + fundBookBalance + "]");
                                        serialBean.setTransSummary("销账金额[" + fundBookBalance + "]");
                                        serialBean.setTotalAmount(fundBookBalance);
                                        /* 3.4.4 如果账本中月不充足，则销掉该账本的金额，将账本更新为0，账单未销账金额为（账单金额-账本金额） */
                                        int resultFundBook = this.deductFundBook(detailBean,
                                                serialBean, tenantId, bookId, fundBookBalance,
                                                thisMonth, conn);
                                        settleTotal = fundBookBalance;
                                        total += fundBookBalance;
                                        writeOffMonth = billMonth;
                                        int resultCharge = this.updataChargeBalance(tenantId,
                                                billMonth, feeSubject, acctId, settleTotal, conn);
                                        if (resultFundBook == 0 || resultCharge == 0) {
                                            isSuccess = false;
                                        }
                                    }
                                    /* 4.记录销账明细 */

                                    String settleDetailSerial = SeqUtil.getNewId(
                                            AmcConstants.SeqName.AMC_SETTLE_DETAIL$SERIAL_CODE$SEQ,
                                            10);
                                    AmcSettleDetailBean amcSettleDetailBean = new AmcSettleDetailBean();
                                    amcSettleDetailBean.setAcctId(chargeBean.getAcctId());
                                    amcSettleDetailBean.setBookId(amcFundBookBean.getBookId());
                                    amcSettleDetailBean.setBusiOperCode("");
                                    amcSettleDetailBean.setChargeSeq(chargeBean.getChargeSeq());
                                    amcSettleDetailBean.setCreateTime(now);
                                    amcSettleDetailBean.setCycleMonth(billMonth);
                                    amcSettleDetailBean.setFeeSubjectId(Long.parseLong(feeSubject));
                                    amcSettleDetailBean.setFundSubjectId(subjectId);
                                    amcSettleDetailBean.setInvoiceSeq(chargeBean.getChargeSeq());
                                    amcSettleDetailBean.setLastStatusDate(now);
                                    amcSettleDetailBean.setSettleType(0);
                                    amcSettleDetailBean.setSettlOrder(0);
                                    amcSettleDetailBean.setSerialcode(settleDetailSerial);
                                    amcSettleDetailBean.setSubsId(chargeBean.getSubsId());
                                    amcSettleDetailBean.setSvcType(0);
                                    amcSettleDetailBean.setTenantId(tenantId);
                                    amcSettleDetailBean.setTotal(settleTotal);
                                    int resultSettleDetail = this.saveSettleDetailBean(
                                            amcSettleDetailBean, thisMonth, conn);
                                    if (resultSettleDetail == 0) {
                                        isSuccess = false;
                                    }
                                }

                            }
                        }
                    }
                }
            }

            /* 3.一层寻缘遍历账本列表，处理通用账本 */
            for (AmcFundBookBean amcFundBookBean : fundBookList) {
                Timestamp effectDate = amcFundBookBean.getEffectDate();// 失效日期
                Timestamp expireDate = amcFundBookBean.getExpireDate();// 生效日期
                Long subjectId = amcFundBookBean.getSubjectId();
                Long bookId = amcFundBookBean.getBookId();
                long fundBookBalance = amcFundBookBean.getBalance();
                Map<String, String> subjectMap = this.querySubject(tenantId, subjectId + "",
                        cacheClient, client);
                if (fundBookBalance == 0 ) {
                    continue;
                }
                if ("1".equals(subjectMap.get("can_settle_all"))) {
                    /* 二层循环，遍历可销账月份 */
                    T2: for (Map<String, Object> map : writeOffMonthList) {
                        String billMonth = (String) map.get("yyyyMM");
                        Integer effectMonth = Integer.parseInt(new SimpleDateFormat("yyyyMM")
                                .format(effectDate.getTime()));
                        Integer expireMonth = Integer.parseInt(new SimpleDateFormat("yyyyMM")
                                .format(expireDate.getTime()));
                        Integer billMonthInt = Integer.parseInt(billMonth);
                        /* 账本失效，则不能使用该账本 */
                        if (billMonthInt < effectMonth || effectMonth > expireMonth) {
                            LOG.info("账本[" + bookId + "]已失效");
                            continue T2;
                        } else {
                            /* 查询账单列表 */
                            List<AmcChargeBean> chargeList = this.queryChargeList(tenantId, acctId,
                                    billMonth, conn);
                            for (AmcChargeBean chargeBean : chargeList) {
                                long chargeBalance = chargeBean.getBalance();
                                String feeSubject = chargeBean.getSubjectId() + "";
                                long settleTotal = 0;
                                if (fundBookBalance == 0 || chargeBalance == 0) {
                                    /* 3.4.1 账本余额为0，则什么都不处理 */
                                    continue;
                                }
                                /** 初始化账本变更记录 */
                                String paySerialCode = SeqUtil.getNewId(
                                        AmcConstants.SeqName.AMC_FUND_SERIAL$PAY_SERIAL_CODE$SEQ,
                                        10);
                                String fundDetailCode = SeqUtil.getNewId(
                                        AmcConstants.SeqName.AMC_FUND_DETAIL$SERIAL_CODE$SEQ, 10);
                                AmcFundDetailBean detailBean = new AmcFundDetailBean();
                                detailBean.setAcctId(acctId);
                                detailBean.setBalancePre(fundBookBalance);
                                detailBean.setBookId(bookId);
                                detailBean.setCreateTime(now);
                                detailBean.setCustId(amcFundBookBean.getCustId());
                                detailBean.setOptType("0");
                                detailBean.setPaySerialCode(paySerialCode);
                                detailBean.setSubjectId(amcFundBookBean.getSubjectId());
                                detailBean.setSerialCode(fundDetailCode);

                                AmcFundSerialBean serialBean = new AmcFundSerialBean();
                                serialBean.setAcctId1(acctId);
                                serialBean.setAcctName1("");
                                serialBean.setAcctId2(null);
                                serialBean.setAcctName2("");
                                serialBean.setCustId1(amcFundBookBean.getCustId());
                                serialBean.setOptType("0");
                                serialBean.setPayRuleId(0);
                                serialBean.setCancelSerialCode("0");
                                serialBean.setPayStatus("0");
                                serialBean.setTenantId(tenantId);
                                serialBean.setPaySerialCode(paySerialCode);

                                /* 3.4 判断余额是否充足 */
                                if (fundBookBalance >= chargeBalance) {
                                    detailBean.setRemark("销账金额[" + chargeBalance + "]");
                                    detailBean.setTransSummary("销账金额[" + chargeBalance + "]");
                                    detailBean.setTotalAmount(chargeBalance);

                                    serialBean.setRemark("销账金额[" + chargeBalance + "]");
                                    serialBean.setTransSummary("销账金额[" + chargeBalance + "]");
                                    serialBean.setTotalAmount(chargeBalance);
                                    /* 3.4.3 如果账本中月充足，则销掉该账单的金额，将账本金额为（账本金额-账单金额），账单未销账金额更新为0 */
                                    int resultFundBook = this.deductFundBook(detailBean,
                                            serialBean, tenantId, bookId, chargeBalance, thisMonth,
                                            conn);
                                    settleTotal = chargeBalance;
                                    total += chargeBalance;
                                    int resultCharge = this.updataChargeBalance(tenantId,
                                            billMonth, feeSubject, acctId, settleTotal, conn);
                                    if (resultFundBook == 0 || resultCharge == 0) {
                                        isSuccess = false;
                                    }
                                    fundBookBalance = fundBookBalance - chargeBalance;
                                } else {
                                    detailBean.setRemark("销账金额[" + fundBookBalance + "]");
                                    detailBean.setTransSummary("销账金额[" + fundBookBalance + "]");
                                    detailBean.setTotalAmount(fundBookBalance);

                                    serialBean.setRemark("销账金额[" + fundBookBalance + "]");
                                    serialBean.setTransSummary("销账金额[" + fundBookBalance + "]");
                                    serialBean.setTotalAmount(fundBookBalance);
                                    /* 3.4.4 如果账本中月不充足，则销掉该账本的金额，将账本更新为0，账单未销账金额为（账单金额-账本金额） */
                                    int resultFundBook = this.deductFundBook(detailBean,
                                            serialBean, tenantId, bookId, fundBookBalance,
                                            thisMonth, conn);
                                    settleTotal = fundBookBalance;
                                    total += fundBookBalance;
                                    writeOffMonth = billMonth;
                                    int resultCharge = this.updataChargeBalance(tenantId,
                                            billMonth, feeSubject, acctId, settleTotal, conn);
                                    if (resultFundBook == 0 || resultCharge == 0) {
                                        isSuccess = false;
                                    }
                                    fundBookBalance = 0;
                                }
                                /* 4.记录销账明细 */

                                String settleDetailSerial = SeqUtil.getNewId(
                                        AmcConstants.SeqName.AMC_SETTLE_DETAIL$SERIAL_CODE$SEQ, 10);
                                AmcSettleDetailBean amcSettleDetailBean = new AmcSettleDetailBean();
                                amcSettleDetailBean.setAcctId(chargeBean.getAcctId());
                                amcSettleDetailBean.setBookId(amcFundBookBean.getBookId());
                                amcSettleDetailBean.setBusiOperCode("");
                                amcSettleDetailBean.setChargeSeq(chargeBean.getChargeSeq());
                                amcSettleDetailBean.setCreateTime(now);
                                amcSettleDetailBean.setCycleMonth(billMonth);
                                amcSettleDetailBean.setFeeSubjectId(Long.parseLong(feeSubject));
                                amcSettleDetailBean.setFundSubjectId(subjectId);
                                amcSettleDetailBean.setInvoiceSeq(0);
                                amcSettleDetailBean.setLastStatusDate(now);
                                amcSettleDetailBean.setSettleType(0);
                                amcSettleDetailBean.setSettlOrder(0);
                                amcSettleDetailBean.setSerialcode(settleDetailSerial);
                                amcSettleDetailBean.setSubsId(chargeBean.getSubsId());
                                amcSettleDetailBean.setSvcType(0);
                                amcSettleDetailBean.setTenantId(tenantId);
                                amcSettleDetailBean.setTotal(settleTotal);
                                int resultSettleDetail = this.saveSettleDetailBean(
                                        amcSettleDetailBean, thisMonth, conn);
                                if (resultSettleDetail == 0) {
                                    isSuccess = false;
                                }
                            }

                        }
                    }
                }
            }
            if (total > 0) {
                /* 5.记录销账流水 */
                AmcSettleLogBean amcSettleLogBean = new AmcSettleLogBean();
                amcSettleLogBean.setAcctId(acctId);
                amcSettleLogBean.setBusiOperCode("");
                amcSettleLogBean.setCancelSerialCode("");
                amcSettleLogBean.setCreateTime(now);
                amcSettleLogBean.setLastStatusDate(now);
                amcSettleLogBean.setSerialCode(settleLogSerial);
                amcSettleLogBean.setSettleMode(0);
                amcSettleLogBean.setSettleType(0);
                amcSettleLogBean.setStatus(0);
                amcSettleLogBean.setTenantId(tenantId);
                amcSettleLogBean.setTotal(total);
                int resultSettleLog = this.saveSettleLogBean(amcSettleLogBean, thisMonth, conn);
                if (resultSettleLog == 0) {
                    isSuccess = false;
                }
                /* 6.更新欠费总表 */
                int resultOw = this.updateOweInfo(tenantId, acctId, total,writeOffMonth, conn);
                if (resultOw == 0) {
                    isSuccess = false;
                }
                if (isSuccess) {
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new BusinessException(AmcConstants.FailConstant.FAIL_CODE_OWE, "销账失败");
                }
            }
        } catch (Exception e) {
            LOG.error("销账异常：[" + e.getMessage() + "]", e);
            try {
                conn.rollback();
            } catch (SQLException e1) {
                LOG.error("销账异常：[" + e1.getMessage() + "]", e1);
            }
        }
        return isSuccess;
    }

    /**
     * 查询账本列表
     * 
     * @return
     * @author LiangMeng
     */
    private List<AmcFundBookBean> queryFundBookList(String tenantId, String acctId, Connection conn) {
        LOG.info("查询账本列表...");
        List<AmcFundBookBean> list = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select tenant_id as tenantId,cust_id as custId,acct_id as acctId,book_id as bookId,subject_type as subjectType,");
        sql.append("subject_id as subjectId,balance as balance,feature_code as featureCode,book_status as bookStatus,");
        sql.append("effect_date as effectDate,expire_date as expireDate,create_time as createTime,subs_freeze_id as subsFreezeId,subs_id as subsId ");
        sql.append("from amc_fund_book where acct_id = ");
        sql.append(acctId);
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        try {
            if (conn != null) {
                list = JdbcTemplate.query(sql.toString(), conn,
                        new BeanListHandler<AmcFundBookBean>(AmcFundBookBean.class));
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }
        return list;
    }

    /**
     * 获取未销账月份列表
     * 
     * @param acctId
     * @return
     * @author LiangMeng
     */
    public List<Map<String, Object>> queryWriteOffMonths(String tenantId, String acctId,
            Connection conn) {
        List<Map<String, Object>> list = null;
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
            if (conn != null) {
                List<AmcOweInfoBean> result = JdbcTemplate.query(sql.toString(), conn,
                        new BeanListHandler<AmcOweInfoBean>(AmcOweInfoBean.class));
                if (result != null && result.size() > 0) {
                    amcOweInfoBean = result.get(0);
                    String month = amcOweInfoBean.getMonth();
                    if (month == null) {
                        throw new SystemException("获取最后未销账月份出错");
                    } else {
                        list = DateUtil.getPerMonth(month);
                    }
                }
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }
        return list;
    }

    /**
     * 查询当前账单
     * 
     * @param data
     * @return
     * @author LiangMeng
     */
    private List<AmcChargeBean> queryChargeList(String tenantId, String acctId, String billMonth,
            Connection conn) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select charge_seq as chargeSeq,acct_id as acctId,subs_id as subsId,service_id as serviceId,subject_id as subjectId,total_amount as totalAmount,");
        sql.append("       adjust_afterwards as adjustAfterwards,disc_total_amount as discTotalAmount,balance,pay_status as payStatus,");
        sql.append("       last_pay_date as lastPayDate,cust_id as custId,cust_type as custType,tenant_id as tenantId ");
        sql.append(" from amc_charge_" + billMonth + " ");
        sql.append(" where acct_id=" + acctId + " ");
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        LOG.info("账单查询语句：[" + sql + "]");
        List<AmcChargeBean> list = null;
        try {
            if (conn != null) {
                list = JdbcTemplate.query(sql.toString(), conn, new BeanListHandler<AmcChargeBean>(
                        AmcChargeBean.class));
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }
        return list;
    }

    /**
     * 根据费用科目查询资金科目
     * 
     * @param feeSubject
     * @return
     * @author LiangMeng
     */
    private String queryFeeSubject(String tenantId, String fundSubject, Connection conn) {
        String feeSubject = null;
        String sql = "select a.fund_subject as feeSubject from amc_deduct_rule a  where a.FUND_SUBJECT='"
                + fundSubject + "' and TENANT_ID='" + tenantId + "'";
        try {
            if (conn != null) {
                LOG.info("销账规则查询sql:[" + sql + "]");
                List<AmcDeductRuleBean> list = JdbcTemplate.query(sql, conn,
                        new BeanListHandler<AmcDeductRuleBean>(AmcDeductRuleBean.class));
                if (list == null || list.size() == 0) {
                    throw new SystemException("999999", "根据资金科目[" + fundSubject + "],未查询到消费科目");
                } else if (list.size() > 1) {
                    throw new SystemException("999999", "根据资金科目[" + fundSubject + "],查询到多个消费科目");
                } else {
                    AmcDeductRuleBean amcDeductRuleBean = list.get(0);
                    feeSubject = amcDeductRuleBean.getFeeSubject();
                }
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }

        return feeSubject;
    }

    /**
     * 更新账本余额
     * 
     * @param tenantId
     * @param acctId
     * @param balance
     * @param conn
     * @return
     * @author LiangMeng
     */
    private int deductFundBook(AmcFundDetailBean amcFundDetailBean,
            AmcFundSerialBean amcFundSerialBean, String tenantId, long bookId, long balance,
            String billMonth, Connection conn) throws Exception {
        int result = 0;
        try {
            /* 1.更新账本余额 */
            StringBuffer sql = new StringBuffer();
            sql.append("update amc_fund_book set balance = balance-");
            sql.append(balance);
            sql.append(" where book_id=");
            sql.append(bookId);
            LOG.info("更新账本余额sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
            /* 2.写账本变更记录 */
            this.saveFundDetailBean(amcFundDetailBean, billMonth, conn);
            /* 3.写流水表 */
            this.saveFundSerialBean(amcFundSerialBean, billMonth, conn);
        } catch (Exception e) {
            LOG.error("更新账本余额异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        return result;
    }

    /**
     * 保存账本变更记录表
     * 
     * @param amcFundDetailBean
     * @param billMonth
     * @param conn
     * @return
     * @throws Exception
     * @author LiangMeng
     */
    private int saveFundDetailBean(AmcFundDetailBean amcFundDetailBean, String billMonth,
            Connection conn) throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_fund_detail_" + billMonth
                    + "(serial_code,pay_serial_code,opt_type,cust_id,acct_id,");
            sql.append("        book_id,subject_id,balance_pre,total_amount,trans_summary,remark,value_date,create_time)");
            sql.append("        values (");
            sql.append(amcFundDetailBean.getSerialCode());
            sql.append(",'");
            sql.append(amcFundDetailBean.getPaySerialCode());
            sql.append("','");
            sql.append(amcFundDetailBean.getOptType());
            sql.append("',");
            sql.append(amcFundDetailBean.getCustId());
            sql.append(",");
            sql.append(amcFundDetailBean.getAcctId());
            sql.append(",");
            sql.append(amcFundDetailBean.getBookId());
            sql.append(",");
            sql.append(amcFundDetailBean.getSubjectId());
            sql.append(",");
            sql.append(amcFundDetailBean.getBalancePre());
            sql.append(",");
            sql.append(amcFundDetailBean.getTotalAmount());
            sql.append(",'");
            sql.append(amcFundDetailBean.getTransSummary());
            sql.append("','");
            sql.append(amcFundDetailBean.getRemark());
            sql.append("',");
            sql.append("now()");
            sql.append(",");
            sql.append("now()");
            sql.append(")");
            LOG.info("记录[amc_fund_detail_" + billMonth + "]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账记录异常：[" + e.getMessage() + "]", e);
            throw e;
        }

        return result;
    }

    private int saveFundSerialBean(AmcFundSerialBean amcFundSerialBean, String billMonth,
            Connection conn) throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_fund_serial_" + billMonth
                    + "(tenant_id,pay_serial_code,peer_serial_code,");
            sql.append("       cancel_serial_code,opt_type,total_amount,trans_summary,pay_rule_id,pay_status,cust_id1,");
            sql.append("      acct_id1,acct_name1,cust_id2,acct_id2,acct_name2,remark,create_time,pay_time,last_status_date)");
            sql.append("        values ('");
            sql.append(amcFundSerialBean.getTenantId());
            sql.append("','");
            sql.append(amcFundSerialBean.getPaySerialCode());
            sql.append("','");
            sql.append(amcFundSerialBean.getPeerSerialcode());
            sql.append("','");
            sql.append(amcFundSerialBean.getCancelSerialCode());
            sql.append("',");
            sql.append(amcFundSerialBean.getOptType());
            sql.append(",");
            sql.append(amcFundSerialBean.getTotalAmount());
            sql.append(",'");
            sql.append(amcFundSerialBean.getTransSummary());
            sql.append("',");
            sql.append(amcFundSerialBean.getPayRuleId());
            sql.append(",");
            sql.append(amcFundSerialBean.getPayStatus());
            sql.append(",");
            sql.append(amcFundSerialBean.getCustId1());
            sql.append(",");
            sql.append(amcFundSerialBean.getAcctId1());
            sql.append(",'");
            sql.append(amcFundSerialBean.getAcctName1());
            sql.append("',");
            sql.append(amcFundSerialBean.getCustId2());
            sql.append(",");
            sql.append(amcFundSerialBean.getAcctId2());
            sql.append(",'");
            sql.append(amcFundSerialBean.getAcctName2());
            sql.append("','");
            sql.append(amcFundSerialBean.getRemark());
            sql.append("',");
            sql.append("now()");
            sql.append(",");
            sql.append("now()");
            sql.append(",");
            sql.append("now()");
            sql.append(")");
            LOG.info("记录[amc_fund_serial_" + billMonth + "]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账记录异常：[" + e.getMessage() + "]", e);
            throw e;
        }

        return result;
    }

    /**
     * 更新账单未销账金额
     * 
     * @param tenantId
     * @param acctId
     * @param balance
     * @param conn
     * @return
     * @author LiangMeng
     */
    private int updataChargeBalance(String tenantId, String billMonth, String subjectId,
            String acctId, long balance, Connection conn) throws Exception {
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
     * 
     * @param amcSettleLogBean
     * @return
     * @author LiangMeng
     */
    private int saveSettleLogBean(AmcSettleLogBean amcSettleLogBean, String billMonth,
            Connection conn) throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_settle_log_" + billMonth
                    + "(serial_code,tenant_id,busi_oper_code,acct_id,");
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
            sql.append(",'");
            sql.append(amcSettleLogBean.getCancelSerialCode());
            sql.append("',");
            sql.append("now()");
            sql.append(")");
            LOG.info("记录[amc_settle_log" + billMonth + "]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账记录异常：[" + e.getMessage() + "]", e);
            throw e;
        }

        return result;
    }

    /**
     * 新增销账流水明细
     * 
     * @param amcSettleDetailBean
     * @return
     * @author LiangMeng
     */
    private int saveSettleDetailBean(AmcSettleDetailBean amcSettleDetailBean, String billMonth,
            Connection conn) throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append(" insert into amc_settle_detail_" + billMonth
                    + "(serial_code,tenant_id,busi_oper_code,acct_id,");
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
            LOG.info("记录[amc_settle_detail" + billMonth + "]表，sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("插入销账明细异常：[" + e.getMessage() + "]", e);
            throw e;
        }

        return result;
    }

    /**
     * 更新欠费总表金额
     * 
     * @param tenantId
     * @param acctId
     * @param balance
     * @param conn
     * @return
     * @throws Exception
     * @author LiangMeng
     */
    private int updateOweInfo(String tenantId, String acctId, long balance,String billMonth, Connection conn)
            throws Exception {
        int result = 0;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("update amc_owe_info");
            sql.append(" set balance = balance-");
            sql.append(balance);
            sql.append(" ,confirm_time = now() where acct_id=");
            sql.append(acctId);
            sql.append(" and tenant_id ='");
            sql.append(tenantId);
            sql.append("'");
            LOG.info("更新欠费总表金额sql：[" + sql + "]");
            result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
        } catch (Exception e) {
            LOG.error("更新欠费总表金额异常：[" + e.getMessage() + "]", e);
            throw e;
        }
        return result;
    }
    /**
     * 
     * @param tenantId
     * @param productId
     * @param cacheClient
     * @param client
     * @return
     * @author LiangMeng
     */
    private Map<String, String> querySubject(String tenantId, String subjectId,
            ICacheClient cacheClient, IDshmClient client) throws BusinessException {
        Map<String, String> params = new TreeMap<String, String>();
        params.put(AmcConstants.FmtFeildName.TENANT_ID, tenantId);
        params.put("subject_id", subjectId);
        List<Map<String, String>> results = client.list(AmcConstants.CacheConfig.GN_SUBJECT_FUND)
                .where(params).executeQuery(cacheClient);
        if (results == null || results.size() == 0) {
            throw new BusinessException("999999", "资金科目[" + subjectId + "]查询不存在");
        } else if (results.size() > 1) {
            throw new BusinessException("999999", "资金科目[" + subjectId + "]重复定义");
        }

        return results.get(0);
    }
    /**
     * 更新未销账账期
     * @param tenantId
     * @param acctId
     * @param balance
     * @param writeOffMonthList
     * @param conn
     * @return
     * @throws Exception
     * @author LiangMeng
     */
    public int updateOweInfoMonth(String tenantId, String acctId, List<Map<String, Object>> writeOffMonthList, Connection conn)
            throws Exception {
        int result = 0;
        try {
            conn.setAutoCommit(false);
            String firstMonth = (String)writeOffMonthList.get(0).get("yyyyMM");
            String month = (String)writeOffMonthList.get(0).get("yyyyMM");
            for(Map<String, Object> map : writeOffMonthList){
                long balance = this.queryChargeBalance(tenantId, acctId, (String)map.get("yyyyMM"), conn);
                LOG.info("获取["+map.get("yyyyMM")+"]月账单欠费金额为["+balance+"]");
                month = (String)map.get("yyyyMM");
                if(balance>0){
                    break;
                }
            }
            if(Long.parseLong(month)>Long.parseLong(firstMonth)){
                StringBuffer sql = new StringBuffer();
                sql.append("update amc_owe_info");
                sql.append(" set month = '");
                sql.append(month);
                sql.append("' where acct_id=");
                sql.append(acctId);
                sql.append(" and tenant_id ='");
                sql.append(tenantId);
                sql.append("'");
                LOG.info("更新欠费总表最后未销账账期sql：[" + sql + "]");
                result = DBUtil.saveOrUpdate(sql.toString(), conn, false);
            }else{
                LOG.info("最后未销账月无需更新");
            }
            
            if(result>0){
                conn.commit();
            }else{
                conn.rollback();
            }
        } catch (Exception e) {
            LOG.error("更新欠费总表最后未销账账期：[" + e.getMessage() + "]", e);
            throw e;
        }
        return result;
    }
    /**
     * 查询账单欠费金额
     * @param tenantId
     * @param acctId
     * @param billMonth
     * @param conn
     * @return
     * @author LiangMeng
     */
    private long queryChargeBalance(String tenantId, String acctId, String billMonth,
            Connection conn) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select IFNULL(sum(balance),0) as balance");
        sql.append(" from amc_charge_" + billMonth + " ");
        sql.append(" where acct_id=" + acctId + " ");
        sql.append(" and tenant_id ='");
        sql.append(tenantId);
        sql.append("'");
        LOG.info("账单查询语句：[" + sql + "]");
        List<AmcChargeBean> list = null;
        try {
            if (conn != null) {
                list = JdbcTemplate.query(sql.toString(), conn, new BeanListHandler<AmcChargeBean>(
                        AmcChargeBean.class));
            } else {
                throw new SystemException("999999", "未取得数据库的连接");
            }
        } catch (Exception e) {
            LOG.error("账单查询报错", e);
        }
        long balance = 0;
        if(list!=null){
            balance = list.get(0).getBalance();
        }
        return balance;
    }
}
