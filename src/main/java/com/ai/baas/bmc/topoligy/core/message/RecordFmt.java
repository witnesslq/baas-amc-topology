package com.ai.baas.bmc.topoligy.core.message;

import org.apache.commons.lang.StringUtils;

import com.ai.baas.bmc.topoligy.core.util.BmcConstants;

public class RecordFmt {
	private RecordFmtKey recordFmtKey;
	private String tenantId;
	private String serviceId;
	private String source;
	private String formatType;
	private Integer fieldSerial;
	private String fieldName;
	private String fieldCode;
	private String dataType;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public Integer getFieldSerial() {
		return fieldSerial;
	}

	public void setFieldSerial(Integer fieldSerial) {
		this.fieldSerial = fieldSerial;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public static class RecordFmtKey {
		private String tenantId;
		private String serviceId;
		private String source;
		
		public RecordFmtKey(String tenantId,String serviceId,String source){
			this.tenantId = tenantId;
			this.serviceId = serviceId;
			this.source = source;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((serviceId == null) ? 0 : serviceId.hashCode());
			result = prime * result
					+ ((source == null) ? 0 : source.hashCode());
			result = prime * result
					+ ((tenantId == null) ? 0 : tenantId.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RecordFmtKey other = (RecordFmtKey) obj;
			if (serviceId == null) {
				if (other.serviceId != null)
					return false;
			} else if (!serviceId.equals(other.serviceId))
				return false;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			if (tenantId == null) {
				if (other.tenantId != null)
					return false;
			} else if (!tenantId.equals(other.tenantId))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if(StringUtils.isNotBlank(tenantId)){
				sb.append(tenantId).append(BmcConstants.COMMON_SPLIT);
			}
			if(StringUtils.isNotBlank(serviceId)){
				sb.append(serviceId).append(BmcConstants.COMMON_SPLIT);
			}
			if(StringUtils.isNotBlank(source)){
				sb.append(source).append(BmcConstants.COMMON_SPLIT);
			}
			return sb.substring(0, sb.length()-1).toString();
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}

		public String getServiceId() {
			return serviceId;
		}

		public void setServiceId(String serviceId) {
			this.serviceId = serviceId;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}
	}
	
	public RecordFmtKey getRecordFmtKey(){
		if (recordFmtKey == null){
			recordFmtKey = new RecordFmtKey(tenantId,serviceId,source);
		}
		return recordFmtKey;
	}

}
