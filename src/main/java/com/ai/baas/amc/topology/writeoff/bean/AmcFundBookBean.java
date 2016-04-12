package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcFundBookBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String tenantId       ;
    private String custId         ;
    private String acctId         ;
    private long bookId           ;
    private String subjectType    ;
    private long subjectId      ;
    private long balance        ;
    private String featureCode    ;
    private String bookStatus     ;
    private Timestamp effectDate     ;
    private Timestamp expireDate     ;
    private Timestamp createTime     ;
    private long subsFreezeId   ;
    private long subsId         ;
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
    public String getSubjectType() {
        return subjectType;
    }
    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }
    public long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }
    public long getBalance() {
        return balance;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }
    public String getFeatureCode() {
        return featureCode;
    }
    public void setFeatureCode(String featureCode) {
        this.featureCode = featureCode;
    }
    public String getBookStatus() {
        return bookStatus;
    }
    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }
    public Timestamp getEffectDate() {
        return effectDate;
    }
    public void setEffectDate(Timestamp effectDate) {
        this.effectDate = effectDate;
    }
    public Timestamp getExpireDate() {
        return expireDate;
    }
    public void setExpireDate(Timestamp expireDate) {
        this.expireDate = expireDate;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    public long getSubsFreezeId() {
        return subsFreezeId;
    }
    public void setSubsFreezeId(long subsFreezeId) {
        this.subsFreezeId = subsFreezeId;
    }
    public long getSubsId() {
        return subsId;
    }
    public void setSubsId(long subsId) {
        this.subsId = subsId;
    }
    

}
