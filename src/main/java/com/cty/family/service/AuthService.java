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
import com.cty.family.dao.AuthDao;
import com.cty.family.entity.AuthEntity;

/**
 * 权限业务类
 * @author 陈天熠
 *
 */
@Service
public class AuthService {
	
	private Logger logger = LoggerFactory.getLogger(AuthService.class);
	
	@Autowired
	private AuthDao authDao;
	
	/**
	 * 查询所有权限信息
	 * @return
	 */
	public List<AuthEntity> getAllAuthInfo() {
		
		List<AuthEntity> authList;
		try {
			authList = authDao.queryAll();
		} catch (Exception e) {
			logger.error("查询所有权限信息异常！", e);
			return null;
		}
		return authList;
	}
	
	/**
	 * 根据id查询单一权限信息
	 * @param id
	 * @return
	 */
	@Cacheable(value = "auth", key = "#id")
	public AuthEntity getAuthInfoById(Integer id) {
		
		AuthEntity auth;
		try {
			auth = authDao.queryById(id);
		} catch (Exception e) {
			logger.error("根据id查询单一权限信息异常！", e);
			return null;
		}
		return auth;
	}
	
	/**
	 * 根据name查询单一权限信息
	 * @param name
	 * @return
	 */
	@Cacheable(value = "auth", key = "#name")
	public AuthEntity getAuthInfoByName(String name) {
		
		AuthEntity auth;
		try {
			auth = authDao.queryByName(name);
		} catch (Exception e) {
			logger.error("根据name查询单一权限信息异常！", e);
			return null;
		}
		return auth;
	}
	
	/**
	 * 添加权限
	 * @param auth
	 * @return
	 */
	public Map<String, String> addAuth(AuthEntity auth) {
		
		logger.info("待添加权限：" + auth.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "添加权限成功！权限：" + auth.toString());
		
		// 参数校验
		if(!verifyAuth(auth)) {
			logger.info("添加权限，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int addResult = authDao.addAuth(auth);
			if(addResult != 1) {
				logger.info("向数据库添加权限失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库添加权限失败！");
			} else {
				logger.info("成功添加权限：" + auth.toString());

			}
		} catch (Exception e) {
			logger.error("向数据库添加权限失败！权限：" + auth.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库添加权限失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 修改权限
	 * @param auth
	 * @return
	 */
	@CacheEvict(value = "auth", key = "#auth.id")
	public Map<String, String> updateAuth(AuthEntity auth) {
		
		logger.info("待修改权限：" + auth.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "修改权限成功！权限：" + auth.toString());
		
		// 参数校验
		if(!verifyAuth(auth)) {
			logger.info("修改权限，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 执行入库
		try {
			int updateResult = authDao.updateAuth(auth);
			if(updateResult != 1) {
				logger.info("向数据库修改权限失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库修改权限失败！");
			} else {
				logger.info("成功修改权限：" + auth.toString());
			}
		} catch (Exception e) {
			logger.error("向数据库修改权限失败！权限：" + auth.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库修改权限失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 删除权限
	 * @param id
	 * @return
	 */
	@CacheEvict(value = "auth", key = "#id")
	public Map<String, String> deleteAuth(Integer id) {
		
		logger.info("待删除权限id：" + id);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "删除权限成功！权限id：" + id);
		
		try {
			authDao.deleteAuth(id);
		} catch (Exception e) {
			logger.error("向数据库删除权限失败！权限id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库删除权限失败！");
		}
		return resultMap;
	}
	
	/**
	 * 权限参数校验
	 * @param auth
	 * @return
	 */
	public boolean verifyAuth(AuthEntity auth) {
		
		String name = auth.getName();
		String desc = auth.getDesc();
		
		// 非空参数校验
		if(StringUtils.isEmpty(name)) {
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

}

