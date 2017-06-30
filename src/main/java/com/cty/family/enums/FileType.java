package com.cty.family.enums;

public enum FileType {

	FILE("FILE", "所有类型文件"),
	IMAGE("IMAGE", "图片类型文件"),
	NOT_IMAGE_FILE("NOT_IMAGE_FILE", "非图片类型文件");
	
	private String key;
	private String value;
	
	// 构造方法
	private FileType(String key, String value) {
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

