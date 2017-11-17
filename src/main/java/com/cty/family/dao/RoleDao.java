package com.cty.family.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.RoleEntity;

@Mapper
public interface RoleDao {

	/**
	 * 查询所有角色信息
	 * @return
	 */
	public List<RoleEntity> queryAll();

	/**
	 * 根据角色id查询单一角色信息
	 * @param id
	 * @return
	 */
	public RoleEntity queryById(Integer id);
	
	/**
	 * 根据角色简称查询单一角色信息
	 * @param name
	 * @return
	 */
	public RoleEntity queryByName(String name);
	
	/**
	 * 根据角色全称查询单一角色信息
	 * @param fullName
	 * @return
	 */
	public RoleEntity queryByFullName(String fullName);

	/**
	 * 添加角色信息
	 * @param role
	 * @return
	 */
	public int addRole(RoleEntity role);
	
	/**
	 * 修改角色信息
	 * @param role
	 * @return
	 */
	public int updateRole(RoleEntity role);

	/**
	 * 删除角色信息
	 * @param id
	 * @return
	 */
	public int deleteRole(Integer id);

}
