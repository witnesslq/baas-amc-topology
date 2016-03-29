package com.ai.baas.amc.topology.core.bean;

import java.io.Serializable;
import java.util.Date;

public class AmcInvoiceBean implements Serializable {

	private static final long serialVersionUID = -1050401238763087301L;
	private Long invoiceSeq      ;   //帐单编号        
	private Long acctId          ;   //帐户标识        
	private Long subsId          ;   //订购标识        
	private String serviceId       ; //  缴费号码          
	private Long totalAmount     ;   //帐单原始总额      
	private Long adjustAfterwards;   //临时调整总额      
	private Long discTotalAmount;    //帐单优惠总额      
	private Long balance          ;  // 未销余额           
	private Long payStatus       ;   //销帐状态        
	private Date lastPayDate    ;    //最后销帐日期      
	private Long printTimes      ;   //打印次数        
	private Long custId          ;   //客户标识        
	private Long custType        ;   //客户类型 
    public Long getInvoiceSeq() {
        return invoiceSeq;
    }
    public void setInvoiceSeq(Long invoiceSeq) {
        this.invoiceSeq = invoiceSeq;
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
    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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
    public Long getPrintTimes() {
        return printTimes;
    }
    public void setPrintTimes(Long printTimes) {
        this.printTimes = printTimes;
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
