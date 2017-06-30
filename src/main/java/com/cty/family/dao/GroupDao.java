package com.cty.family.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

import com.cty.family.entity.GroupEntity;

@Mapper
public interface GroupDao {

	/**
	 * 查询所有群组信息
	 * @return
	 */
	public List<GroupEntity> queryAll();

	/**
	 * 根据群组id查询单一群组信息
	 * @param id
	 * @return
	 */
	public GroupEntity queryById(Integer id);
	
	/**
	 * 根据群组name查询单一群组信息
	 * @param name
	 * @return
	 */
	public GroupEntity queryByName(String name);

	/**
	 * 根据用户id查询用户所在全部群组信息
	 * @param userId
	 * @return
	 */
	public List<GroupEntity> queryAllByUserId(Integer userId);

	/**
	 * 添加群组信息
	 * @param group
	 * @return
	 */
	public int addGroup(GroupEntity group);
	
	/**
	 * 修改群组信息
	 * @param group
	 * @return
	 */
	public int updateGroup(GroupEntity group);

	/**
	 * 删除群组信息
	 * @param id
	 * @return
	 */
	public int deleteGroup(Integer id);
	
	/**
	 * 添加群组成员
	 * @param params
	 * @return
	 */
	public int addGroupMembers(Map<String, Object> params);

	/**
	 * 删除群组成员
	 * @param groupId
	 */
	public void deleteGroupMembers(Integer groupId);

}
