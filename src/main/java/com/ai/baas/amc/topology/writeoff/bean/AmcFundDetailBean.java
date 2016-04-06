package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcFundDetailBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String serialCode     ;
    private String paySerialCode ;
    private String optType        ;
    private String custId         ;
    private String acctId         ;
    private long bookId         ;
    private long subjectId      ;
    private long balancePre     ;
    private long totalAmount    ;
    private String transSummary   ;
    private String remark          ;
    private Timestamp valueDate      ;
    private Timestamp createTime     ;
    public String getSerialCode() {
        return serialCode;
    }
    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }
    public String getPaySerialCode() {
        return paySerialCode;
    }
    public void setPaySerialCode(String paySerialCode) {
        this.paySerialCode = paySerialCode;
    }
    public String getOptType() {
        return optType;
    }
    public void setOptType(String optType) {
        this.optType = optType;
    }
    public String getCustId() {
        return custId;
    }
    public void setCustId(String custId) {
        this.custId = custId;
    }
    public String getAcctId() {
        return acctId;
    }
    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }
    public long getBookId() {
        return bookId;
    }
    public void setBookId(long bookId) {
        this.bookId = bookId;
    }
    public long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }
    public long getBalancePre() {
        return balancePre;
    }
    public void setBalancePre(long balancePre) {
        this.balancePre = balancePre;
    }
    public long getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getTransSummary() {
        return transSummary;
    }
    public void setTransSummary(String transSummary) {
        this.transSummary = transSummary;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public Timestamp getValueDate() {
        return valueDate;
    }
    public void setValueDate(Timestamp valueDate) {
        this.valueDate = valueDate;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
}
