package com.dhkun.test.mybatis.constants;

public enum DefaultField {

	DISABLED("disabled", "0"),
	DISABLED_INGORED("disabled", null),
	CREATED("created", "now()"),
	CREATED_INGORED("created", null), // 使用null表示忽略
	UPDATED("modified", "now()");
	
	
	private String field;
	private String defaultValue;
	
	DefaultField(String field, String defaultValue) {
		this.field = field;
		this.defaultValue = defaultValue;
	}

	public String getField() {
		return field;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
}
