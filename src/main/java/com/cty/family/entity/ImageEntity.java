package com.cty.family.entity;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 图片信息实体类
 * @author 陈天熠
 *
 */
public class ImageEntity implements Serializable {

	private static final long serialVersionUID = 127619859003720358L;

	private Integer id;
	private String name;
	private byte[] content;
	private String desc;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ImageEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", content=");
		builder.append(Arrays.toString(content));
		builder.append(", desc=");
		builder.append(desc);
		builder.append("]");
		return builder.toString();
	}
	
}
