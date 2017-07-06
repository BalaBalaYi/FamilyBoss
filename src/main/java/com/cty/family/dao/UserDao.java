package com.cty.family.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.UserEntity;

/**
 * 用户数据访问类
 * @author 陈天熠
 *
 */
@Mapper
public interface UserDao {

	/**
	 * 查询所有用户信息
	 * @return
	 */
	public List<UserEntity> queryAll();
	
	/**
	 * 查询所有开启状态的用户信息
	 * @return
	 */
	public List<UserEntity> queryAllOn();

	/**
	 * 查询所有在线的用户信息
	 * @return
	 */
	public List<UserEntity> queryAllOnlineInfo();
	
	/**
	 * 查询所有男性用户信息
	 * @return
	 */
	public List<UserEntity> queryAllMale();
	
	/**
	 * 查询所有女性用户信息
	 * @return
	 */
	public List<UserEntity> queryAllFemale();
	
	/**
	 * 查询所有用户ID和NAME信息
	 * @return
	 */
	public List<UserEntity> queryAllIdAndName();
	
	/**
	 * 根据主键id查询用户信息
	 * @param id
	 * @return
	 */
	public UserEntity queryById(Integer id);
	
	/**
	 * 根据用户姓名查询用户信息
	 * @param name
	 * @return
	 */
	public UserEntity queryByName(String name);
	
	/**
	 * 查询没有群组的所有用户信息
	 * @return
	 */
	public List<UserEntity> queryWithOutGroup();
	
	/**
	 * 根据群组id查询用户信息
	 * @param id
	 * @return
	 */
	public List<UserEntity> queryByGroupId(Integer id);
	
	/**
	 * 根据群组id查询组内用户ID和NAME信息
	 * @param id
	 * @return
	 */
	public List<UserEntity> queryAllIdAndNameByGroupId(Integer id);
	
	/**
	 * 新增用户信息
	 * @param user
	 * @return
	 */
	public int addUser(UserEntity user);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	public int updateUser(UserEntity user);
	
	/**
	 * 修改用户签名
	 * @param params
	 * @return
	 */
	public int updateUserSign(Map<String, Object> params);
	
	/**
	 * 删除用户信息
	 * @param id
	 * @return
	 */
	public int deleteUser(Integer id);
	
	/**
	 * 开启用户账户
	 * @param id
	 * @return
	 */
	public int onUser(Integer id);
	
	/**
	 * 关闭用户账户
	 * @param id
	 * @return
	 */
	public int offUser(Integer id);

}
