package com.ai.baas.amc.topology.writeoff.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class AmcDeductRuleBean implements Serializable {

    private static final long serialVersionUID = -1050401238763087301L;

    private String tenantId;

    private String fundSubject;

    private String feeSubject;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getFundSubject() {
        return fundSubject;
    }

    public void setFundSubject(String fundSubject) {
        this.fundSubject = fundSubject;
    }

    public String getFeeSubject() {
        return feeSubject;
    }

    public void setFeeSubject(String feeSubject) {
        this.feeSubject = feeSubject;
    }

}
