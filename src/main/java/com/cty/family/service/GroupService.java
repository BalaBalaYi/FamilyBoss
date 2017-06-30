package com.cty.family.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cty.family.dao.GroupDao;
import com.cty.family.entity.GroupEntity;
import com.cty.family.entity.UserEntity;

/**
 * 群组业务类
 * @author 陈天熠
 *
 */
@Service
public class GroupService {
	
	private Logger logger = LoggerFactory.getLogger(GroupService.class);
	
	@Autowired
	private GroupDao groupDao;
	
	/**
	 * 查询所有群组信息
	 * @return
	 */
	public List<GroupEntity> getAllGroupsInfo() {
		
		List<GroupEntity> groupList;
		try {
			groupList = groupDao.queryAll();
		} catch (Exception e) {
			logger.error("查询所有群组信息异常！", e);
			return null;
		}
		return groupList;
	}
	
	/**
	 * 根据id查询单一群组信息
	 * @param id
	 * @return
	 */
	@Cacheable(value = "group", key = "#id")
	public GroupEntity getGroupInfoById(Integer id) {
		
		GroupEntity group;
		try {
			group = groupDao.queryById(id);
		} catch (Exception e) {
			logger.error("根据id查询单一群组信息异常！", e);
			return null;
		}
		return group;
	}
	
	/**
	 * 根据name查询单一群组信息
	 * @param name
	 * @return
	 */
	@Cacheable(value = "group", key = "#name")
	public GroupEntity getGroupInfoById(String name) {
		
		GroupEntity group;
		try {
			group = groupDao.queryByName(name);
		} catch (Exception e) {
			logger.error("根据name查询单一群组信息异常！", e);
			return null;
		}
		return group;
	}
	
	/**
	 * 根据用户id查询用户所在全部群组信息
	 * @param userId 用户id
	 */
	public List<GroupEntity> getGroupsInfoByUserId(Integer userId) {
		
		List<GroupEntity> groupList;
		
		try {
			groupList = groupDao.queryAllByUserId(userId);
		} catch (Exception e) {
			logger.error("根据用户id查询用户所在群组信息异常！", e);
			return null;
		}
		return groupList;
	}

	/**
	 * 添加群组
	 * @param group
	 * @return
	 */
	public Map<String, String> addGroup(GroupEntity group) {
		
		logger.info("待添加群组：" + group.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "添加群组成功！群组：" + group.toString());
		
		// 参数校验
		if(!verifyGroup(group)) {
			logger.info("添加群组，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int addResult = groupDao.addGroup(group);
			if(addResult != 1) {
				logger.info("向数据库添加群组失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库添加群组失败！");
			} else {
				logger.info("成功添加群组：" + group.toString());

			}
		} catch (Exception e) {
			logger.error("向数据库添加群组失败！群组：" + group.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库添加群组失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 修改群组
	 * @param group
	 * @return
	 */
	@CacheEvict(value = "group", key = "#group.id")
	public Map<String, String> updateGroup(GroupEntity group) {
		
		logger.info("待修改群组：" + group.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "修改群组成功！群组：" + group.toString());
		
		// 参数校验
		if(!verifyGroup(group)) {
			logger.info("修改群组，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int updateResult = groupDao.updateGroup(group);
			if(updateResult != 1) {
				logger.info("向数据库修改群组失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库修改群组失败！");
			} else {
				logger.info("成功修改群组：" + group.toString());
			}
		} catch (Exception e) {
			logger.error("向数据库修改群组失败！群组：" + group.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库修改群组失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除群组
	 * @param id
	 * @return
	 */
	@CacheEvict(value = "group", key = "#id")
	public Map<String, String> deleteGroup(Integer id) {
		
		logger.info("待删除群组id：" + id);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "删除群组成功！群组id：" + id);
		
		try {
			groupDao.deleteGroup(id);
		} catch (Exception e) {
			logger.error("向数据库删除群组失败！群组id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库删除群组失败！");
		}
		return resultMap;
	}
	
	/**
	 * 群组参数校验
	 * @param user
	 * @return
	 */
	public boolean verifyGroup(GroupEntity group) {
		
		String name = group.getName();
		String desc = group.getDesc();
		String status = group.getStatus();
		
		// 非空参数校验
		if(StringUtils.isEmpty(name) || StringUtils.isEmpty(status)) {
			logger.info("必要参数为空！");
			return false;
		}
		// 字符及数字大小校验
		if(name.length() > 16 || desc.length() > 128) {
			logger.info("参数长度超限！");
			return false;
		}
		
		return true;
	}

	/**
	 * 筛选出组内的成员并加标记，与全体成员一同返回
	 * @param all 全体成员
	 * @param groupMembers 组内成员
	 * @return
	 */
	public List<Map<String, Object>> filterGroupMember(List<UserEntity> all, List<UserEntity> groupMembers) {
		
		List<Map<String, Object>> filterList = new ArrayList<Map<String, Object>>();
		
		for(UserEntity user : all){
			Map<String, Object> singleUser = new HashMap<String, Object>();
			singleUser.put("user", user);
			singleUser.put("flag", false);
			for(UserEntity member : groupMembers){
				if(member.getId() == user.getId()){
					singleUser.put("flag", true);
				}
			}
			filterList.add(singleUser);
		}
		
		return filterList;
	}

	/**
	 * 配置群组成员
	 * @param groupId
	 * @param modifyList
	 * @return
	 */
	@Transactional
	public Map<String, String> modifyGroup(Integer groupId, List<Integer> idList) {
		
		logger.info("待配置群组id：" + groupId);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "配置群组成功！群组id：" + groupId);
		
		// 执行入库
		try {
			// 1.删除现有成员
			groupDao.deleteGroupMembers(groupId);
			
			// 2.根据 modifyList 重新添加成员
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("groupId", groupId);
			params.put("idList", idList);
			int addResult = groupDao.addGroupMembers(params);
			
			if(addResult != idList.size()) {
				logger.info("向数据库配置群组失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库配置群组失败！");
			} else {
				logger.info("成功配置群组！群组id：" + groupId);
			}
		} catch (Exception e) {
			logger.error("向数据库配置群组失败！群组id：" + groupId, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库配置群组失败！");
		}
		
		return resultMap;
	}

}

