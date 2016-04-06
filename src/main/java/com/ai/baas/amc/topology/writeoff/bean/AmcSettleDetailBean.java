package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcSettleDetailBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String serialcode      ;
    private String tenantId        ;
    private String busiOperCode    ;
    private long acctId          ;
    private int settleMode      ;
    private int settleType      ;
    private long bookId          ;
    private long subsId          ;
    private int svcType         ;
    private long fundSubjectId   ;
    private String cycleMonth      ;
    private long invoiceSeq      ;
    private long chargeSeq       ;
    private long feeSubjectId    ;
    private long total           ;
    private Timestamp createTime      ;
    private int status          ;
    private Timestamp lastStatusDate  ;
    private long settlOrder      ;
    public String getSerialcode() {
        return serialcode;
    }
    public void setSerialcode(String serialcode) {
        this.serialcode = serialcode;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getBusiOperCode() {
        return busiOperCode;
    }
    public void setBusiOperCode(String busiOperCode) {
        this.busiOperCode = busiOperCode;
    }
    public long getAcctId() {
        return acctId;
    }
    public void setAcctId(long acctId) {
        this.acctId = acctId;
    }
    public int getSettleMode() {
        return settleMode;
    }
    public void setSettleMode(int settleMode) {
        this.settleMode = settleMode;
    }
    public int getSettleType() {
        return settleType;
    }
    public void setSettleType(int settleType) {
        this.settleType = settleType;
    }
    public long getBookId() {
        return bookId;
    }
    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
    public long getSubsId() {
        return subsId;
    }
    public void setSubsId(long subsId) {
        this.subsId = subsId;
    }
    public int getSvcType() {
        return svcType;
    }
    public void setSvcType(int svcType) {
        this.svcType = svcType;
    }
    public long getFundSubjectId() {
        return fundSubjectId;
    }
    public void setFundSubjectId(long fundSubjectId) {
        this.fundSubjectId = fundSubjectId;
    }
    public String getCycleMonth() {
        return cycleMonth;
    }
    public void setCycleMonth(String cycleMonth) {
        this.cycleMonth = cycleMonth;
    }
    public long getInvoiceSeq() {
        return invoiceSeq;
    }
    public void setInvoiceSeq(long invoiceSeq) {
        this.invoiceSeq = invoiceSeq;
    }
    public long getChargeSeq() {
        return chargeSeq;
    }
    public void setChargeSeq(long chargeSeq) {
        this.chargeSeq = chargeSeq;
    }
    public long getFeeSubjectId() {
        return feeSubjectId;
    }
    public void setFeeSubjectId(long feeSubjectId) {
        this.feeSubjectId = feeSubjectId;
    }
    public long getTotal() {
        return total;
    }
    public void setTotal(long total) {
        this.total = total;
    }
    
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    public Timestamp getLastStatusDate() {
        return lastStatusDate;
    }
    public void setLastStatusDate(Timestamp lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }
    public long getSettlOrder() {
        return settlOrder;
    }
    public void setSettlOrder(long settlOrder) {
        this.settlOrder = settlOrder;
    }

}
