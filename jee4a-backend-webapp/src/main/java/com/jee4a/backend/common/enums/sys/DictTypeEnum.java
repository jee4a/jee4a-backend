package com.jee4a.backend.common.enums.sys;

/**
 * @description 查询条件类型
 * @date 2018年4月4日
 */

public enum DictTypeEnum {
	LARGE_CUSTOMER_RIGHTS("large_customer_rights", "大客户权限");
	private String k;
	private String v;

	private DictTypeEnum(String key, String value) {
		this.k = key;
		this.v = value;
	}

	public String getK() {
		return k;
	}

	public String getV() {
		return v;
	}
}
