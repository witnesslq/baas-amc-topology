package com.ai.baas.amc.topology.core.util;

public class AmcConstants {

	public static final String FIELD_SPLIT = new String(new char[] { (char) 1 });
	public static final String RECORD_SPLIT = new String(new char[] { (char) 2 });
	
	public static final String SERVICE_ID = "service_id";
	public static final String TENANT_ID = "tenant_id";
	public static final String SOURCE = "source";
	public static final String BATCH_SERIAL_NUMBER = "bsn";
	public static final String SERIAL_NUMBER = "sn";
	public static final String ACCOUNT_PERIOD = "account_period";
	public static final String RECORD_DATA = "data";
	public static final String SUBS_ID = "subs_id";
	
	
	public static final String COMMON_SPLIT = ",";
	public static final String COMMON_JOINER = "_";
	public static final String COMMON_HYPHEN = "-";
	
	public static final String UNPACKING_BOLT ="unpacking";
	public static final String DUPLICATE_CHECKING_BOLT = "duplicate_checking";
	public static final String RULE_ADAPT_BOLT = "rule_adapt";
	public static final String COST_CALCULATING_BOLT = "cost_calculating";

	
	
}
