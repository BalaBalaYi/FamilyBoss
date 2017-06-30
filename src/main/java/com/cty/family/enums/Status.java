package com.cty.family.enums;

public enum Status {

	ON("ON", "开启"),
	OFF("OFF", "关闭");
	
	private String key;
	private String value;
	
	// 构造方法
	private Status(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	// get/set
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return this.key + " : " + this.value;
	}
	
}

