package com.ai.baas.amc.topology.core.bean;

import java.io.Serializable;
import java.util.Date;

public class AmcChargeBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private Long chargeSeq       ; // 费用明细编号          
    private Long acctId             ;//  账户ID                
    private Long subsId          ; // 订购标识\群组标识        
    private Long serviceId       ; // 服务号码             
    private Long subjectId       ; // 科目代码             
    private Long totalAmount     ; // 原始科目总额           
    private Long adjustAfterwards; // 临时调整总额           
    private Long discTotalAmount; // 优惠总额             
    private Long balance          ; // 未销余额             
    private Long payStatus       ; // 销帐状态             
    private Date lastPayDate     ;//  最后销帐日期          
    private Long custId             ;//  客户标识              
    private Long custType        ; // 客户类型    
    public Long getChargeSeq() {
        return chargeSeq;
    }
    public void setChargeSeq(Long chargeSeq) {
        this.chargeSeq = chargeSeq;
    }
    public Long getAcctId() {
        return acctId;
    }
    public void setAcctId(Long acctId) {
        this.acctId = acctId;
    }
    public Long getSubsId() {
        return subsId;
    }
    public void setSubsId(Long subsId) {
        this.subsId = subsId;
    }
    public Long getServiceId() {
        return serviceId;
    }
    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
    public Long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    public Long getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
    public Long getAdjustAfterwards() {
        return adjustAfterwards;
    }
    public void setAdjustAfterwards(Long adjustAfterwards) {
        this.adjustAfterwards = adjustAfterwards;
    }
    public Long getDiscTotalAmount() {
        return discTotalAmount;
    }
    public void setDiscTotalAmount(Long discTotalAmount) {
        this.discTotalAmount = discTotalAmount;
    }
    public Long getBalance() {
        return balance;
    }
    public void setBalance(Long balance) {
        this.balance = balance;
    }
    public Long getPayStatus() {
        return payStatus;
    }
    public void setPayStatus(Long payStatus) {
        this.payStatus = payStatus;
    }   
    public Date getLastPayDate() {
        return lastPayDate;
    }
    public void setLastPayDate(Date lastPayDate) {
        this.lastPayDate = lastPayDate;
    }
    public Long getCustId() {
        return custId;
    }
    public void setCustId(Long custId) {
        this.custId = custId;
    }
    public Long getCustType() {
        return custType;
    }
    public void setCustType(Long custType) {
        this.custType = custType;
    }
   
    
}
