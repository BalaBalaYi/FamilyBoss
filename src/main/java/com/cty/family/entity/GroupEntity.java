package com.cty.family.entity;

import java.io.Serializable;

/**
 * 群组信息实体类
 * @author 陈天熠
 *
 */
public class GroupEntity implements Serializable {

	private static final long serialVersionUID = -2454891486571972122L;

	private Integer id;
	private String name;
	private String desc;
	private String status;
	private String imgName;
	private String imgUrl;
	
	
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getImgName() {
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GroupEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", desc=");
		builder.append(desc);
		builder.append(", status=");
		builder.append(status);
		builder.append(", imgName=");
		builder.append(imgName);
		builder.append(", imgUrl=");
		builder.append(imgUrl);
		builder.append("]");
		return builder.toString();
	}
	
}
