package com.cty.family.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.UserLoginEntity;

@Mapper
public interface UserLoginDao {

	/**
	 * 查询所有登录记录
	 * @return UserLoginEntity
	 */
	public UserLoginEntity queryAll();
	
	/**
	 * 查询所有在线的用户
	 * @return List<UserLoginEntity>
	 */
	public List<UserLoginEntity> queryAllOnline();
	
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
