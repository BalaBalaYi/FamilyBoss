package com.cty.family.service;

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
import com.cty.family.dao.RoleDao;
import com.cty.family.entity.RoleEntity;

/**
 * 角色业务类
 * @author 陈天熠
 *
 */
@Service
public class RoleService {
	
	private Logger logger = LoggerFactory.getLogger(RoleService.class);
	
	@Autowired
	private RoleDao roleDao;
	
	/**
	 * 查询所有角色信息
	 * @return
	 */
	public List<RoleEntity> getAllRoleInfo() {
		
		List<RoleEntity> roleList;
		try {
			roleList = roleDao.queryAll();
		} catch (Exception e) {
			logger.error("查询所有橘色信息异常！", e);
			return null;
		}
		return roleList;
	}
	
	/**
	 * 根据id查询单一角色信息
	 * @param id
	 * @return
	 */
	@Cacheable(value = "role", key = "#id")
	public RoleEntity getRoleInfoById(Integer id) {
		
		RoleEntity role;
		try {
			role = roleDao.queryById(id);
		} catch (Exception e) {
			logger.error("根据id查询单一角色信息异常！", e);
			return null;
		}
		return role;
	}
	
	/**
	 * 根据角色简称查询单一角色信息
	 * @param name
	 * @return
	 */
	public RoleEntity getRoleInfoByName(String name) {
		
		RoleEntity role;
		try {
			role = roleDao.queryByName(name);
		} catch (Exception e) {
			logger.error("根据角色简称查询单一角色信息异常！", e);
			return null;
		}
		return role;
	}
	
	/**
	 * 根据角色全称查询单一角色信息
	 * @param fullName
	 * @return
	 */
	public RoleEntity getRoleInfoByFullName(String fullName) {
		
		RoleEntity role;
		try {
			role = roleDao.queryByFullName(fullName);
		} catch (Exception e) {
			logger.error("根据角色全称查询单一角色信息异常！", e);
			return null;
		}
		return role;
	}
	
	/**
	 * 添加角色
	 * @param role
	 * @return
	 */
	public Map<String, String> addRole(RoleEntity role) {
		
		logger.info("待添加角色：" + role.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "添加角色成功！角色：" + role.toString());
		
		// 参数校验
		if(!verifyRole(role)) {
			logger.info("添加角色，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int addResult = roleDao.addRole(role);
			if(addResult != 1) {
				logger.info("向数据库添加角色失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库添加角色失败！");
			} else {
				logger.info("成功添加角色：" + role.toString());

			}
		} catch (Exception e) {
			logger.error("向数据库添加角色失败！角色：" + role.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库添加角色失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 修改角色
	 * @param role
	 * @return
	 */
	@CacheEvict(value = "role", key = "#role.id")
	public Map<String, String> updateRole(RoleEntity role) {
		
		logger.info("待修改角色：" + role.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "修改角色成功！角色：" + role.toString());
		
		// 参数校验
		if(!verifyRole(role)) {
			logger.info("修改角色，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int updateResult = roleDao.updateRole(role);
			if(updateResult != 1) {
				logger.info("向数据库修改角色失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库修改角色失败！");
			} else {
				logger.info("成功修改角色：" + role.toString());
			}
		} catch (Exception e) {
			logger.error("向数据库修改角色失败！角色：" + role.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库修改角色失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除角色
	 * @param id
	 * @return
	 */
	@CacheEvict(value = "role", key = "#id")
	public Map<String, String> deleteRole(Integer id) {
		
		logger.info("待删除角色id：" + id);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "删除角色成功！角色id：" + id);
		
		try {
			// 删除角色关联信息
			
			
			// 删除角色信息表信息
			roleDao.deleteRole(id);
		} catch (Exception e) {
			logger.error("向数据库删除角色失败！角色id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库删除角色失败！");
		}
		return resultMap;
	}
	
	/**
	 * 角色参数校验
	 * @param role
	 * @return
	 */
	public boolean verifyRole(RoleEntity role) {
		
		String name = role.getName();
		String fullName = role.getFullName();
		String desc = role.getDesc();
		
		// 非空参数校验
		if(StringUtils.isEmpty(name) || StringUtils.isEmpty(fullName)) {
			logger.info("必要参数为空！");
			return false;
		}
		// 字符及数字大小校验
		if(name.length() > 8 || fullName.length() > 32 || desc.length() > 128) {
			logger.info("参数长度超限！");
			return false;
		}
		
		return true;
	}

}

