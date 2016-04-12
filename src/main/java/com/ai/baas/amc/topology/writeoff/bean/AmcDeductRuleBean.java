package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcDeductRuleBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;

    private String tenantId;

    private String fundAubject;

    private String feeAubject;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFundAubject() {
        return fundAubject;
    }

    public void setFundAubject(String fundAubject) {
        this.fundAubject = fundAubject;
    }

    public String getFeeAubject() {
        return feeAubject;
    }

    public void setFeeAubject(String feeAubject) {
        this.feeAubject = feeAubject;
    }

}
