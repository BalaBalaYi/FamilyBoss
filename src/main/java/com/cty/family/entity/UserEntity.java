package com.cty.family.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息实体类
 * @author 陈天熠
 *
 */
public class UserEntity implements Serializable {

	private static final long serialVersionUID = -8478889245089278642L;
	
	private Integer id;
	private String name;
	private String password;
	private String sex;
	private Integer age;
	private Date birth;
	private String address;
	private String phone;
	private String email;
	private Integer fatherId;
	private Integer motherId;
	private String desc;
	private Integer type; // 0:根账户（不可删除）,1：普通账户
	private String status; // ON,OFF
	private String imgName; // 个人头像(存储image表的name字段)
	private String imgUrl; // 个人头像(存储服务器端存储路径)
	private String im; // im开启状态：ON,OFF
	private String sign;
	
	private String reserved1;
	private String reserved2;
	
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getFatherId() {
		return fatherId;
	}
	public void setFatherId(Integer fatherId) {
		this.fatherId = fatherId;
	}
	public Integer getMotherId() {
		return motherId;
	}
	public void setMotherId(Integer motherId) {
		this.motherId = motherId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
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
	public String getIm() {
		return im;
	}
	public void setIm(String im) {
		this.im = im;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getReserved1() {
		return reserved1;
	}
	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}
	public String getReserved2() {
		return reserved2;
	}
	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserEntity [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", password=");
		builder.append(password);
		builder.append(", sex=");
		builder.append(sex);
		builder.append(", age=");
		builder.append(age);
		builder.append(", birth=");
		builder.append(birth);
		builder.append(", address=");
		builder.append(address);
		builder.append(", phone=");
		builder.append(phone);
		builder.append(", email=");
		builder.append(email);
		builder.append(", fatherId=");
		builder.append(fatherId);
		builder.append(", motherId=");
		builder.append(motherId);
		builder.append(", desc=");
		builder.append(desc);
		builder.append(", type=");
		builder.append(type);
		builder.append(", status=");
		builder.append(status);
		builder.append(", imgName=");
		builder.append(imgName);
		builder.append(", imgUrl=");
		builder.append(imgUrl);
		builder.append(", im=");
		builder.append(im);
		builder.append(", sign=");
		builder.append(sign);
		builder.append(", reserved1=");
		builder.append(reserved1);
		builder.append(", reserved2=");
		builder.append(reserved2);
		builder.append("]");
		return builder.toString();
	}
	
}
