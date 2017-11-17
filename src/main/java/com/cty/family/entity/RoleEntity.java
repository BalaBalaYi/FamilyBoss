package com.cty.family.entity;

import java.io.Serializable;

/**
 * 角色信息实体类
 * @author 陈天熠
 *
 */
public class RoleEntity implements Serializable {

	private static final long serialVersionUID = -7099617055087980495L;
	
	private Integer id;
	private String name;
	private String fullName;
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
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
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
		builder.append("RoleEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", desc=");
		builder.append(desc);
		builder.append("]");
		return builder.toString();
	}
	
}
