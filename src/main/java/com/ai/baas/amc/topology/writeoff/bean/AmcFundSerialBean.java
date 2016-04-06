package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcFundSerialBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String tenantId        ; 
    private String systemId        ;
    private String paySerialCode   ;
    private String peerSerialcode  ;
    private String cancelSerialCode;
    private String optType         ;
    private long totalAmount     ;
    private String transSummary    ;
    private long payRuleId       ;
    private String payStatus       ;
    private String custId1         ;
    private String acctId1         ;
    private String acctName1       ;
    private String custId2         ;
    private String acctId2         ;
    private String acctName2       ;
    private Timestamp createTime      ;
    private Timestamp payTime         ;
    private Timestamp lastStatusDate  ;
    private String remark          ;
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getSystemId() {
        return systemId;
    }
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
    public String getPaySerialCode() {
        return paySerialCode;
    }
    public void setPaySerialCode(String paySerialCode) {
        this.paySerialCode = paySerialCode;
    }
    public String getPeerSerialcode() {
        return peerSerialcode;
    }
    public void setPeerSerialcode(String peerSerialcode) {
        this.peerSerialcode = peerSerialcode;
    }
    public String getCancelSerialCode() {
        return cancelSerialCode;
    }
    public void setCancelSerialCode(String cancelSerialCode) {
        this.cancelSerialCode = cancelSerialCode;
    }
    public String getOptType() {
        return optType;
    }
    public void setOptType(String optType) {
        this.optType = optType;
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
    public long getPayRuleId() {
        return payRuleId;
    }
    public void setPayRuleId(long payRuleId) {
        this.payRuleId = payRuleId;
    }
    public String getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }
    public String getCustId1() {
        return custId1;
    }
    public void setCustId1(String custId1) {
        this.custId1 = custId1;
    }
    public String getAcctId1() {
        return acctId1;
    }
    public void setAcctId1(String acctId1) {
        this.acctId1 = acctId1;
    }
    public String getAcctName1() {
        return acctName1;
    }
    public void setAcctName1(String acctName1) {
        this.acctName1 = acctName1;
    }
    public String getCustId2() {
        return custId2;
    }
    public void setCustId2(String custId2) {
        this.custId2 = custId2;
    }
    public String getAcctId2() {
        return acctId2;
    }
    public void setAcctId2(String acctId2) {
        this.acctId2 = acctId2;
    }
    public String getAcctName2() {
        return acctName2;
    }
    public void setAcctName2(String acctName2) {
        this.acctName2 = acctName2;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    public Timestamp getPayTime() {
        return payTime;
    }
    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }
    public Timestamp getLastStatusDate() {
        return lastStatusDate;
    }
    public void setLastStatusDate(Timestamp lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
