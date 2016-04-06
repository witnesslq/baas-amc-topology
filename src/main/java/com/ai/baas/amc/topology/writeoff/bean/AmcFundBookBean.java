package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class AmcFundBookBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;
    private String tenantId        ;
    private String serialCode      ;
    private String busiOperCode    ;
    private String acctId          ;
    private int settleMode      ;
    private int settleType      ;
    private long total           ;
    private int status          ;
    private Timestamp lastStatusDate  ;
    private String cancelSerialCode;
    private Timestamp createTime      ;
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getSerialCode() {
        return serialCode;
    }
    public void setSerialCode(String serialCode) {
        this.serialCode = serialCode;
    }
    public String getBusiOperCode() {
        return busiOperCode;
    }
    public void setBusiOperCode(String busiOperCode) {
        this.busiOperCode = busiOperCode;
    }
    public String getAcctId() {
        return acctId;
    }
    public void setAcctId(String acctId) {
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
    
    public String getCancelSerialCode() {
        return cancelSerialCode;
    }
    public void setCancelSerialCode(String cancelSerialCode) {
        this.cancelSerialCode = cancelSerialCode;
    }
    public Timestamp getLastStatusDate() {
        return lastStatusDate;
    }
    public void setLastStatusDate(Timestamp lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }
    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
    
    

}
