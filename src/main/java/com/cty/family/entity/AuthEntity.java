package com.cty.family.entity;

import java.io.Serializable;

/**
 * 权限信息实体类
 * @author 陈天熠
 *
 */
public class AuthEntity implements Serializable {

	private static final long serialVersionUID = -45488457405776503L;

	private Integer id;
	private String name;
	private String tag;
	private String desc;
	private String parentId;
	private String url;
	private Integer orderNum;
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
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
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
		builder.append("AuthEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", tag=");
		builder.append(tag);
		builder.append(", desc=");
		builder.append(desc);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", url=");
		builder.append(url);
		builder.append(", orderNum=");
		builder.append(orderNum);
		builder.append(", imgName=");
		builder.append(imgName);
		builder.append(", imgUrl=");
		builder.append(imgUrl);
		builder.append("]");
		return builder.toString();
	}
	
}
