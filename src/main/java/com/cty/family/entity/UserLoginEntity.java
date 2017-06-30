package com.cty.family.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录信息实体类
 * @author 陈天熠
 *
 */
public class UserLoginEntity implements Serializable {

	private static final long serialVersionUID = 3567581243308101884L;
	
	private Integer userId;
	private String status;
	private Date logoutTime;
	private Date loginTime;
	private Date lastLoginTime;
	private String loginIp;
	private String lastLoginIp;
	private String loginAddress;
	private String lastLoginAddress;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLogoutTime() {
		return logoutTime;
	}
	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLoginIp() {
		return loginIp;
	}
	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	public String getLoginAddress() {
		return loginAddress;
	}
	public void setLoginAddress(String loginAddress) {
		this.loginAddress = loginAddress;
	}
	public String getLastLoginAddress() {
		return lastLoginAddress;
	}
	public void setLastLoginAddress(String lastLoginAddress) {
		this.lastLoginAddress = lastLoginAddress;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserLoginEntity [userId=");
		builder.append(userId);
		builder.append(", status=");
		builder.append(status);
		builder.append(", logoutTime=");
		builder.append(logoutTime);
		builder.append(", loginTime=");
		builder.append(loginTime);
		builder.append(", lastLoginTime=");
		builder.append(lastLoginTime);
		builder.append(", loginIp=");
		builder.append(loginIp);
		builder.append(", lastLoginIp=");
		builder.append(lastLoginIp);
		builder.append(", loginAddress=");
		builder.append(loginAddress);
		builder.append(", lastLoginAddress=");
		builder.append(lastLoginAddress);
		builder.append("]");
		return builder.toString();
	}
	
}
