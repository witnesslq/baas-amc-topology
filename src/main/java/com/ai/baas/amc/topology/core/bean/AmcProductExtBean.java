package com.ai.baas.amc.topology.core.bean;

import java.io.Serializable;

public class AmcProductExtBean implements Serializable ,Comparable<AmcProductExtBean> {
    
    private static final long serialVersionUID = 2935615819774524354L;
    private String tenantId   ;
    private String productId  ;
    private String productName;
    private String priority   ;
    private String effectDate ;
    private String expireDate ;
    private String status     ;
    private String createTime ;
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getEffectDate() {
        return effectDate;
    }
    public void setEffectDate(String effectDate) {
        this.effectDate = effectDate;
    }
    public String getExpireDate() {
        return expireDate;
    }
    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getCreateTime() {
        return createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    @Override
    public int compareTo(AmcProductExtBean arg0) {
        return this.getPriority().compareTo(arg0.getPriority());
    }
}
