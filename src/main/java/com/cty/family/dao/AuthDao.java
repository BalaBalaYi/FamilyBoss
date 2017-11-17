package com.cty.family.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.AuthEntity;

@Mapper
public interface AuthDao {

	/**
	 * 查询所有权限信息
	 * @return
	 */
	public List<AuthEntity> queryAll();

	/**
	 * 根据权限id查询单一权限信息
	 * @param id
	 * @return
	 */
	public AuthEntity queryById(Integer id);
	
	/**
	 * 根据权限name查询单一权限信息
	 * @param name
	 * @return
	 */
	public AuthEntity queryByName(String name);

	/**
	 * 添加权限信息
	 * @param auth
	 * @return
	 */
	public int addAuth(AuthEntity auth);
	
	/**
	 * 修改权限信息
	 * @param auth
	 * @return
	 */
	public int updateAuth(AuthEntity auth);

	/**
	 * 删除权限信息
	 * @param id
	 * @return
	 */
	public int deleteAuth(Integer id);

}
