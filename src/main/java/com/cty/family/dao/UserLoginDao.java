package com.cty.family.dao;

import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.UserLoginEntity;

@Mapper
public interface UserLoginDao {

	/**
	 * 查询所有登录记录
	 * @return
	 */
	public UserLoginEntity queryAll();
	
	/**
	 * 根据用户id查询登录记录
	 * @param userId
	 * @return
	 */
	public UserLoginEntity queryById(Integer userId);
	
	/**
	 * 根据用户id查询登录状态
	 * @param userId
	 * @return
	 */
	public String queryStatusById(Integer userId);
	
	/**
	 * 添加用户登录记录
	 * @param info
	 * @return
	 */
	public int addUserLoginInfo(UserLoginEntity info);
	
	/**
	 * 修改用户登录记录
	 * @param info
	 * @return
	 */
	public int updateUserLoginInfo(UserLoginEntity info);

	/**
	 * 更新（添加或修改）用户登录记录信息
	 * @param userLoginInfo
	 * @return
	 */
	public int modifyLoginInfo(UserLoginEntity userLoginInfo);
}
