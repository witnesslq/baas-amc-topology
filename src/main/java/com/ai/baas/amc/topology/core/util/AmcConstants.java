package com.ai.baas.amc.topology.core.util;

public class AmcConstants {
	
    /**输入数据格式编码*/
    /*租户ID*/
	public static final String TENANT_ID = "tenant_id";
    /*业务类型*/
    public static final String SERVICE_ID = "service_id";
    /*来源*/
    public static final String SOURCE = "source";
    /*批次号*/
    public static final String BSN = "bsn";
    /*唯一标识*/
    public static final String SN = "sn";
    /*客户ID*/
    public static final String CUST_ID = "cust_id";
    /*用户ID*/
    public static final String SUBS_ID = "subs_id";
    /*账户ID*/
    public static final String ACCT_ID = "acct_id";
    /*开始时间*/
    public static final String START_TIME = "start_time";
    /**/
    public static final String TRADE_SEQ = "trade_seq";
    /*费用*/
	public static final String FEE1 = "fee1";
    /*详单科目*/
    public static final String SUBJECT1 = "subject1";
    /*费用*/
    public static final String FEE2 = "fee2";
    /*详单科目*/
    public static final String SUBJECT2 = "subject2";
    /*费用*/
    public static final String FEE3 = "fee3";
    /*详单科目*/
    public static final String SUBJECT3 = "subject3";
	
	/*查重bolt*/
	public static final String DUPLICATE_CHECKING_BOLT = "duplicate_checking";
	/*账务优惠bolt*/
    public static final String ACCOUNT_PREFERENTIAL_BOLT = "account_preferential";

    /**错误环节定义*/
    /*查重环节*/
    public static final String FAIL_STEP_DUP = "AMC-duplicate-checking";
    
    /**错误编码定义*/
    /*重复数据*/
    public static final String FAIL_CODE_DUP = "AMC-000001";
    
    
}
