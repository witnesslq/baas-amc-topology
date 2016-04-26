package com.ai.baas.amc.topology.core.util;
/**
 * 账务优惠相关配置
 * Date: 2016年3月30日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author LiangMeng
 */
public class AmcConstants {
	
    /**
     * 输入数据格式编码
     * Date: 2016年3月30日 <br>
     * Copyright (c) 2016 asiainfo.com <br>
     * 
     * @author LiangMeng
     */
    public static class FmtFeildName{
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
        

        /*账期*/
        public static final String BILL_MONTH = "bill_month";

        /*开始时间*/
        public static final String ARRIVE_TIME = "arrive_time";
    }
    /**
     * bolt名称配置
     * Date: 2016年3月30日 <br>
     * Copyright (c) 2016 asiainfo.com <br>
     * 
     * @author LiangMeng
     */
	public static class BoltName{
	    /*查重bolt*/
	    public static final String DUPLICATE_CHECKING_BOLT = "duplicate_checking";
	    /*账务优惠bolt*/
	    public static final String ACCOUNT_PREFERENTIAL_BOLT = "account_preferential";
        /*账务优惠bolt*/
        public static final String WRITE_OFF_BOLT = "write_off";
	}
	/**
	 * 错误编码、环节定义
	 * Date: 2016年3月30日 <br>
	 * Copyright (c) 2016 asiainfo.com <br>
	 * 
	 * @author LiangMeng
	 */
	public static class FailConstant{
	    /**错误环节定义*/
	    /*查重环节*/
	    public static final String FAIL_STEP_DUP = "AMC-duplicate-checking";
        /*查重环节*/
        public static final String FAIL_STEP_PRE = "AMC-account-preferential";
        /*销账环节*/
        public static final String FAIL_STEP_OWE = "AMC-account-writeoff";
        
	    /**错误编码定义*/
	    /*重复数据*/
	    public static final String FAIL_CODE_DUP = "AMC-000001";
	    /**/
        public static final String FAIL_CODE_GET_CACHE_DATA = "AMC-000002";
        /**/
        public static final String FAIL_CODE_READ_DB_DATA = "AMC-000003";
        /**/
        public static final String FAIL_CODE_WRITE_DB_DATA = "AMC-000004";
        /**/
        public static final String FAIL_CODE_SEND_KFK_MSG = "AMC-000005";
        /**/
        public static final String FAIL_CODE_OWE = "AMC-000006";
	    
	}
	/**
	 * 优惠产品相关配置
	 * Date: 2016年3月30日 <br>
	 * Copyright (c) 2016 asiainfo.com <br>
	 * 
	 * @author LiangMeng
	 */
	public static class ProductInfo{
	    //产品ID
	    public static final String PRODUCT_ID = "product_id";
        //优惠类型
        public static final String CALC_TYPE = "calc_type";
        //新科目ID用于保底
        public static final String NEW_SUBJECT = "new_subject_id";
        //账单科目
        public static final String BILL_SUBJECT = "bill_subject_id";
        //参考科目
        public static final String REF_SUBJECT = "ref_subject_id";
        
        

        //扩展名称字段
        public static final String EXT_NAME = "ext_name";
        //扩展值字段
        public static final String EXT_VALUE = "ext_value";
        
        //优惠类型:保底
        public static final String CALC_TYPE_BD = "bd";
        //保底金额
        public static final String BD_AMOUNT = "bd_amount";
        //优惠类型:封顶
        public static final String CALC_TYPE_FD = "fd";
        //封顶金额
        public static final String FD_AMOUNT = "fd_amount";
	    
        
        //优惠类型:显示折扣
        public static final String CALC_TYPE_XSZK = "xszk";
        //折扣率
        public static final String XSZK_ZKL = "xszk_zkl";

        //生效日期
        public static final String XSZK_EFFECT_DATE = "xszk_effect_date";

        //失效日期
        public static final String XSZK_EXPIRE_DATE = "xszk_expire_date";
        

        //执行时段标志
        public static final String XSZK_TIME_FLAG = "xszk_time_flag";
        //执行生效时段
        public static final String XSZK_EFFECT_TIME = "xszk_effect_time";
        //执行失效时段
        public static final String XSZK_EXPIRE_TIME = "xszk_expire_time";
        
        /**
         * 产品状态
         * Date: 2016年4月21日 <br>
         * Copyright (c) 2016 asiainfo.com <br>
         * 
         * @author LiangMeng
         */
        public static final class Status {
            
            private Status() {
                
            }
            
            /**
             * 失效
             */
            public static final String INVALID = "0";
            
            /**
             * 生效
             */
            public static final String EFFECTIVE = "1";

            /**
             * 待生效
             */
            public static final String TO_BE_EFFECTIVE = "2";

        }

	}
	/**
	 * 卡夫卡相关配置
	 * Date: 2016年3月30日 <br>
	 * Copyright (c) 2016 asiainfo.com <br>
	 * 
	 * @author LiangMeng
	 */
	public static class KafkaConfig{
	    //broker list
	    public static final String BROKER_LIST = "metadata.broker.list";
	    //broker list
        public static final String SERIALIZER_CLASS = "serializer.class";
        //broker list
        public static final String REQUIRED_ACKS = "request.required.acks";
        //broker list
        public static final String KAFKA_TOPIC_XK = "kafka.topic.xk";
	}
	/**
	 * 缓存表配置
	 * Date: 2016年3月31日 <br>
	 * Copyright (c) 2016 asiainfo.com <br>
	 * 
	 * @author LiangMeng
	 */
	public static class CacheConfig{
	    
	    public static final String CCS_APPNAME = "ccs.appname";
	    
	    public static final String CCS_ZK_ADDRESS = "ccs.zk_address";
        //
        public static final String AMC_DR_BILL_SUBJECT_MAP = "amc_dr_bill_subject_map";
        //
        public static final String BL_SUBS_COMM = "bl_subs_comm";
        //
        public static final String AMC_PRODUCT_INFO = "amc_product_info";
        //
        public static final String AMC_PRODUCT_DETAIL = "amc_product_detail";
        //
        public static final String AMC_PRODUCT_EXT = "amc_product_ext";
        //
        public static final String GN_SUBJECT_FUND = "gn_subject_fund";
        
    }
	/**
	 * 序列名称
	 * Date: 2016年4月12日 <br>
	 * Copyright (c) 2016 asiainfo.com <br>
	 * 
	 * @author LiangMeng
	 */
	public static class SeqName{

        public static final String AMC_CHARGE$SERIAL_CODE$SEQ = "AMC_CHARGE$SERIAL_CODE$SEQ";
        
        public static final String AMC_INVOICE$SERIAL_CODE$SEQ = "AMC_INVOICE$SERIAL_CODE$SEQ";
        
        public static final String AMC_SETTLE_LOG$SERIAL_CODE$SEQ = "AMC_SETTLE_LOG$SERIAL_CODE$SEQ";
        
        public static final String AMC_SETTLE_DETAIL$SERIAL_CODE$SEQ = "AMC_SETTLE_DETAIL$SERIAL_CODE$SEQ";
        
        public static final String AMC_FUND_DETAIL$SERIAL_CODE$SEQ = "AMC_FUND_DETAIL$SERIAL_CODE$SEQ";
        
        public static final String AMC_FUND_SERIAL$PAY_SERIAL_CODE$SEQ = "AMC_FUND_SERIAL$PAY_SERIAL_CODE$SEQ";
        
    }
}
