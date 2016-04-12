package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcOweInfoBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String tenantId     ; 
    private String acctId       ;
    private long balance       ;
    private String month         ;
    private Timestamp createTime   ;
    private Timestamp confirmTime  ;
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getAcctId() {
        return acctId;
    }
    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }
    public long getBalance() {
        return balance;
    }
    public void setBalance(long balance) {
        this.balance = balance;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    public Timestamp getConfirmTime() {
        return confirmTime;
    }
    public void setConfirmTime(Timestamp confirmTime) {
        this.confirmTime = confirmTime;
    }
    
}
