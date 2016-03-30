package com.ai.baas.amc.topology.core.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.baas.storm.failbill.FailureBill;
import com.ai.baas.storm.jdbc.JdbcProxy;
import com.ai.baas.storm.jdbc.JdbcTemplate;
import com.ai.baas.storm.util.BaseConstants;

public class AmcUtil {

    private static Logger LOG = LoggerFactory.getLogger(AmcUtil.class);
    /**
     * 构造错单格式
     * @param inputData
     * @param data
     * @return
     * @author LiangMeng
     */
    public static FailureBill initFailureBill(String inputData,Map<String,String> data){
        FailureBill failureBill = new FailureBill();
        failureBill.setTenantId(data.get(AmcConstants.FmtFeildName.TENANT_ID));
        failureBill.setServiceId(data.get(AmcConstants.FmtFeildName.SERVICE_ID));
        failureBill.setSource(data.get(AmcConstants.FmtFeildName.SOURCE));
        failureBill.setBsn(data.get(AmcConstants.FmtFeildName.BSN));
        failureBill.setSn(data.get(AmcConstants.FmtFeildName.SN));
        //failureBill.setAccountPeriod(data.get(AmcConstants.TENANT_ID));
        failureBill.setArrivalTime(data.get(AmcConstants.FmtFeildName.START_TIME));
        failureBill.setFailStep(data.get(AmcConstants.FailConstant.FAIL_STEP_DUP));
        failureBill.setFailCode(data.get(AmcConstants.FailConstant.FAIL_CODE_DUP));
        failureBill.setFailReason("重复数据");
        failureBill.setFailPakcet(inputData);
        failureBill.setFailDate(new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
        return failureBill;
    }
    /**
     * 执行jdbc更新操作
     * @param sql
     * @return
     * @author LiangMeng
     */
    public static  int excuteSql(String sql) {
        int result = 0;
        Connection conn = null;
        try {
            conn = JdbcProxy.getConnection(BaseConstants.JDBC_DEFAULT);
            result = JdbcTemplate.update(sql, conn);
        } catch (Exception e) {
            LOG.error("JDBC执行异常：["+e.getMessage()+"]",e);
        }
       
        return result;
    }
}
