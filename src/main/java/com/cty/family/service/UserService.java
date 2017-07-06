package com.cty.family.service;

import java.util.ArrayList;
import java.util.Date;
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

import com.cty.family.config.CommonConfig;
import com.cty.family.dao.UserDao;
import com.cty.family.dao.UserLoginDao;
import com.cty.family.entity.UserEntity;
import com.cty.family.entity.UserLoginEntity;
import com.cty.family.enums.Status;
import com.cty.family.util.CipherUtil;
import com.cty.family.util.KeyDigestUtil;

/**
 * 用户业务类
 * @author 陈天熠
 *
 */
@Service
public class UserService {
	
	private Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserDao userDao;
	@Autowired
	private UserLoginDao userLoginDao;
	@Autowired
	private CommonConfig config;

	/**
	 * 用户登录
	 * @param name
	 * @param password
	 * @return
	 */
	public Map<String, Object> login(String name, String password, String ip) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", "0");
		resultMap.put("reason", "登录校验成功！");
		
		// 参数校验
		if(StringUtils.isEmpty(name) || StringUtils.isEmpty(password)) {
			resultMap.put("result", "1");
			resultMap.put("reason", "登录必要参数为空！");
			return resultMap;
		}
		
		// 根据用户名参数查询数据库并对比密码hash值
		UserEntity user = userDao.queryByName(name);
		if(null == user) {
			resultMap.put("result", "2");
			resultMap.put("reason", "用户名或密码错误！");
			return resultMap;
		}
		String inputPassHash = KeyDigestUtil.getKeyDigest(password, config.getDigest_key());
		String dbPassHash = CipherUtil.decrypt(user.getPassword(), config.getCrypt_key());
		if(!inputPassHash.equals(dbPassHash)) {
			resultMap.put("result", "2");
			resultMap.put("reason", "用户名或密码错误！");
			return resultMap;
		}
		if(!"ON".equals(user.getStatus())) {
			resultMap.put("result", "3");
			resultMap.put("reason", "账户为关闭状态，请联系管理员！");
			return resultMap;
		}
		
		// 更新用户登录信息
		boolean updateLoginInfoResult = modifyLoginInfo(user.getId(), ip, 0);
		if(!updateLoginInfoResult){
			logger.error("用户登录，更新用户登录信息失败！");
		}
		
		resultMap.put("user", user);
		return resultMap;
	}
	
	/**
	 * 添加/修改用户登录信息
	 * @param id
	 * @param ip
	 * @param type 0：登录；1：登出
	 * @return
	 */
	@CacheEvict(value = "login_status", key = "#id")
	public boolean modifyLoginInfo(Integer id, String ip, Integer type){
		
		logger.debug("记录" + (type == 0 ? "登录" : "登出") + "信息，用户id：" + id + "，IP地址：" + ip);
		
		UserLoginEntity userLoginInfo = new UserLoginEntity();
		userLoginInfo.setUserId(id);
		
		Date now = new Date();
		
		try {
			if(type == 0){ // 登录
				userLoginInfo.setStatus(Status.ON.getKey());
				userLoginInfo.setLoginIp(ip);
				userLoginInfo.setLoginAddress("");
				userLoginInfo.setLoginTime(now);
			} else { // 登出
				userLoginInfo.setStatus(Status.OFF.getKey());
				userLoginInfo.setLastLoginIp(ip);
				userLoginInfo.setLastLoginAddress("");
				userLoginInfo.setLogoutTime(now);
				// 登出时一定已经存在登录信息，则取回登录信息中的登录时间作为上一次登录时间
				UserLoginEntity lastLoginInfo = userLoginDao.queryById(id);
				userLoginInfo.setLastLoginTime(lastLoginInfo.getLoginTime());
			}
			int result = userLoginDao.modifyLoginInfo(userLoginInfo);
			logger.debug("登录信息：" + userLoginInfo.toString() + ",result:" + result);
			if(result != 1 && result != 2){
				return false;
			}
		} catch (Exception e) {
			logger.error("向数据库添加/修改用户登录信息异常！信息：" + userLoginInfo.toString(), e);
			return false;
		}
		return true;
	}
	
	/**
	 * 查询所有用户信息
	 * @return
	 */
	public List<UserEntity> getAllUsersInfo() {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAll();
		} catch (Exception e) {
			logger.error("查询所有用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 查询所有在线用户信息
	 * @return
	 */
	public List<UserEntity> getAllOnlineUsersInfo() {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAllOnlineInfo();
		} catch (Exception e) {
			logger.error("查询所有在线用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 根据用户id查询在线状态
	 * @param id
	 * @return
	 */
	@Cacheable(value = "login_status", key = "#userId")
	public String getLoginStatus(Integer userId) {
		
		String status = "";
		try {
			status = userLoginDao.queryStatusById(userId);
			if(StringUtils.isEmpty(status)){
				status = "OFF";
			}
		} catch (Exception e) {
			logger.error("根据用户id查询在线状态异常！", e);
			return "OFF";
		}
		return status;
	}
	
	/**
	 * 查询所有用户ID和NAME信息
	 */
	public List<UserEntity> getAllUsersIdAndName() {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAllIdAndName();
		} catch (Exception e) {
			logger.error("查询所有用户ID和NAME信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 查询没有群组的所有用户信息
	 * @return
	 */
	public List<UserEntity> getUsersInfoWithOutGroup() {
		List<UserEntity> userList;
		try {
			userList = userDao.queryWithOutGroup();
		} catch (Exception e) {
			logger.error("查询没有群组的所有用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 根据群组id查询组内用户信息
	 * @param id
	 * @return
	 */
	public List<UserEntity> getUsersInfoByGroupId(Integer id) {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryByGroupId(id);
		} catch (Exception e) {
			logger.error("根据群组id查询组内用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 根据群组id查询组内用户ID和NAME信息
	 * @param id
	 * @return
	 */
	public List<UserEntity> getAllUsersIdAndNameByGroupId(Integer id) {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAllIdAndNameByGroupId(id);
		} catch (Exception e) {
			logger.error("根据群组id查询组内用户ID和NAME信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 根据id查询单一用户信息并直接返回
	 * @param id
	 * @return
	 */
	@Cacheable(value = "user", key = "#id")
	public UserEntity getUserInfoById(Integer id) {
		
		UserEntity user;
		try {
			user = userDao.queryById(id);
			// 密码解密
			user.setPassword(CipherUtil.decrypt(user.getPassword(), config.getCrypt_key()));
		} catch (Exception e) {
			logger.error("根据id查询单一用户信息异常！", e);
			return null;
		}
		return user;
	}
	
	/**
	 * 根据id查询单一用户信息并封装为List<Map>形式返回
	 * @param id
	 * @return
	 */
	@Cacheable(value = "user_detail", key = "#id")
	public List<Map<String, String>> getUserInfoByIdWithMapRet(Integer id) {
		
		List<Map<String, String>> userDetail = new ArrayList<Map<String, String>>();
		UserEntity user;
		try {
			user = userDao.queryById(id);
		} catch (Exception e) {
			logger.error("根据id查询单一用户信息异常！", e);
			return null;
		}
		// 完全自定义封装
		// ID
		Map<String, String> prop1 = new HashMap<String, String>();
		prop1.put("name", "ID");
		prop1.put("value", user.getId().toString());
		userDetail.add(prop1);
		
		// 姓名
		Map<String, String> prop2 = new HashMap<String, String>();
		prop2.put("name", "姓名");
		prop2.put("value", user.getName());
		userDetail.add(prop2);
		
		// 性别
		Map<String, String> prop3 = new HashMap<String, String>();
		prop3.put("name", "性别");
		prop3.put("value", user.getSex());
		userDetail.add(prop3);
		
		// 年龄
		Map<String, String> prop4 = new HashMap<String, String>();
		prop4.put("name", "年龄");
		prop4.put("value", user.getAge().toString());
		userDetail.add(prop4);
		
		// 生日
		Map<String, String> prop5 = new HashMap<String, String>();
		prop5.put("name", "生日");
		prop5.put("value", user.getBirth().toString());
		userDetail.add(prop5);
		
		// 住址
		Map<String, String> prop6 = new HashMap<String, String>();
		prop6.put("name", "住址");
		prop6.put("value", user.getAddress());
		userDetail.add(prop6);
		
		// 电话
		Map<String, String> prop7 = new HashMap<String, String>();
		prop7.put("name", "电话");
		prop7.put("value", user.getPhone());
		userDetail.add(prop7);
		
		// 邮箱
		Map<String, String> prop8 = new HashMap<String, String>();
		prop8.put("name", "邮箱");
		prop8.put("value", user.getEmail());
		userDetail.add(prop8);
		
		// 父亲
		Map<String, String> prop9 = new HashMap<String, String>();
		prop9.put("name", "父亲");
		if(user.getFatherId() != null && user.getFatherId() > 0){
			String fatherName = getUserInfoById(user.getFatherId()).getName();
			prop9.put("value", fatherName);
		} else {
			prop9.put("value", "无");
		}
		userDetail.add(prop9);
		
		// 母亲
		Map<String, String> prop10 = new HashMap<String, String>();
		prop10.put("name", "母亲");
		if(user.getMotherId() != null && user.getMotherId() > 0){
			String motherName = getUserInfoById(user.getMotherId()).getName();
			prop10.put("value", motherName);
		} else {
			prop10.put("value", "无");
		}
		userDetail.add(prop10);
		
		// 个人描述
		Map<String, String> prop11 = new HashMap<String, String>();
		prop11.put("name", "个人描述");
		prop11.put("value", user.getDesc());
		userDetail.add(prop11);
		
		// 账户类型
		Map<String, String> prop12 = new HashMap<String, String>();
		prop12.put("name", "账户类型");
		prop12.put("value", user.getType() == 0 ? "根账户" : "普通账户");
		userDetail.add(prop12);
		
		// 账户状态
		Map<String, String> prop13 = new HashMap<String, String>();
		prop13.put("name", "账户状态");
		prop13.put("value", user.getStatus() == "ON" ? "开启" : "关闭");
		userDetail.add(prop13);
		
		// 头像
		Map<String, String> prop14 = new HashMap<String, String>();
		prop14.put("name", "头像");
		prop14.put("value", user.getImgName());
		userDetail.add(prop14);
		
		// IM账户
		Map<String, String> prop15 = new HashMap<String, String>();
		prop15.put("name", "IM账户");
		prop15.put("value", user.getIm());
		userDetail.add(prop15);
				
		// 签名
		Map<String, String> prop16 = new HashMap<String, String>();
		prop16.put("name", "签名");
		prop16.put("value", user.getSign());
		userDetail.add(prop16);
		
		return userDetail;
	}
	
	/**
	 * 根据name查询单一用户信息
	 * @param name
	 * @return
	 */
	@Cacheable(value = "user", key = "#name")
	public UserEntity getUserInfoByName(String name) {
		
		UserEntity user;
		try {
			user = userDao.queryByName(name);
			// 密码解密
			user.setPassword(CipherUtil.decrypt(user.getPassword(), config.getCrypt_key()));
		} catch (Exception e) {
			logger.error("根据name查询单一用户信息异常！", e);
			return null;
		}
		return user;
	}
	
	/**
	 * 查询所有男性用户信息
	 * @return
	 */
	public List<UserEntity> getMaleUsers() {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAllMale();
		} catch (Exception e) {
			logger.error("查询所有男性用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 查询所有女性用户信息
	 * @return
	 */
	public List<UserEntity> getFemaleUsers() {
		
		List<UserEntity> userList;
		try {
			userList = userDao.queryAllFemale();
		} catch (Exception e) {
			logger.error("查询所有女性用户信息异常！", e);
			return null;
		}
		return userList;
	}
	
	/**
	 * 添加用户
	 * @param user
	 * @return
	 */
	public Map<String, String> addUser(UserEntity user) {
		
		logger.info("待添加用户：" + user.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "添加用户成功！用户：" + user.toString());
		
		// 参数校验
		if(!verifyUser(user)) {
			logger.info("添加用户，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 对密码进行加密
		String password = KeyDigestUtil.getKeyDigest(user.getPassword(), config.getDigest_key());
		password = CipherUtil.encrypt(password, config.getCrypt_key());
		user.setPassword(password);
		
		// 默认签名
		String sign = user.getSign();
		if(null == sign || "".equals(sign)) {
			user.setSign("大家好，我是" + user.getName() + "!");
		}
		
		// 执行入库
		try {
			int addResult = userDao.addUser(user);
			if(addResult != 1) {
				logger.info("向数据库添加用户失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库添加用户失败！");
			} else {
				logger.info("成功添加用户：" + user.toString());

			}
		} catch (Exception e) {
			logger.error("向数据库添加用户失败！用户：" + user.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库添加用户失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 修改用户
	 * @param user
	 * @return
	 */
	@CacheEvict(value = "user", key = "#user.id")
	public Map<String, String> updateUser(UserEntity user) {
		
		logger.info("待修改用户：" + user.toString());
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "修改用户成功！用户：" + user.toString());
		
		// 参数校验
		if(!verifyUser(user)) {
			logger.info("修改用户，参数校验失败！");
			resultMap.put("result", "1");
			resultMap.put("reason", "参数校验失败！");
			return resultMap;
		}
		
		// 对密码进行加密
		String password = CipherUtil.encrypt(user.getPassword(), config.getCrypt_key());
		user.setPassword(password);
		
		// 执行入库
		try {
			int updateResult = userDao.updateUser(user);
			if(updateResult != 1) {
				logger.info("向数据库修改用户失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库修改用户失败！");
			} else {
				logger.info("成功修改用户：" + user.toString());
			}
		} catch (Exception e) {
			logger.error("向数据库修改用户失败！用户：" + user.toString(), e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库修改用户失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 修改用户签名
	 * @param id
	 * @param sign
	 * @return
	 */
	@CacheEvict(value = "user", key = "#id")
	public boolean updateUserSign(Integer id, String sign) {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		params.put("sign", sign);
		
		try {
			int updateResult = userDao.updateUserSign(params);
			if(updateResult != 1) {
				logger.info("向数据库修改用户签名失败！");
				return false;
			}
		} catch (Exception e) {
			logger.error("向数据库修改签名失败！用户id：" + id, e);
			return false;
		}
		return true;
	}
	
	/**
	 * 删除用户
	 * @param id
	 * @return
	 */
	@CacheEvict(value = "user", key = "#id")
	public Map<String, String> deleteUser(Integer id) {
		
		logger.info("待删除用户id：" + id);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "删除用户成功！用户id：" + id);
		
		try {
			// 校验该用户是否可以删除，如果可以则删除
			UserEntity user = userDao.queryById(id);
			if(null != user && user.getType() == 1) {
				userDao.deleteUser(id);
			} else {
				resultMap.put("result", "1");
				resultMap.put("reason", "该用户账户为根账户，无法删除！");
			}
		} catch (Exception e) {
			logger.error("向数据库删除用户失败！用户id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库删除用户失败！");
		}
		return resultMap;
	}
	
	/**
	 * 操作用户账户
	 * @param id 用户账户id
	 * @param type 0：关闭账户；1：开启账户
	 * @return
	 */
	@CacheEvict(value = "user", key = "#id")
	public Map<String, String> doStatus(Integer id, Integer type) {
		
		logger.info("待" + (type == 0 ? "关闭账户" : "开启账户") + "用户id：" + id);
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", (type == 0 ? "关闭账户" : "开启账户") + "成功！用户id：" + id);
		
		// 执行入库
		try {
			// 校验该用户是否可以进行账户状态操作，如果可以则进行
			UserEntity user = userDao.queryById(id);
			if(null != user && user.getType() == 1) {
				int doStatusResult = -1;
				if(type == 0) {
					doStatusResult = userDao.offUser(id);
				} else {
					doStatusResult = userDao.onUser(id);
				}
				if(doStatusResult != 1) {
					logger.info("向数据库" + (type == 0 ? "关闭账户" : "开启账户") + "失败！");
					resultMap.put("result", "2");
					resultMap.put("reason", "向数据库" + (type == 0 ? "关闭账户" : "开启账户") + "失败！");
				} else {
					logger.info("成功" + (type == 0 ? "关闭账户" : "开启账户") + "!用户id：" + id);
				}
			} else {
				resultMap.put("result", "1");
				resultMap.put("reason", "该用户账户为根账户，无法进行账户状态操作！");
			}
			
		} catch (Exception e) {
			logger.error("向数据库" + (type == 0 ? "关闭账户" : "开启账户") + "失败！用户id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库" + (type == 0 ? "关闭账户" : "开启账户") + "失败！");
		}
		
		return resultMap;
	}
	
	/**
	 * 用户参数校验
	 * @param user
	 * @return
	 */
	public boolean verifyUser(UserEntity user) {
		
		String name = user.getName();
		String password = user.getPassword();
		String sex = user.getSex();
		Integer age = user.getAge();
//		Date birth = user.getBirth();
		String phone = user.getPhone();
		String email = user.getEmail();
		Integer type = user.getType();
		String status = user.getStatus();
		String im = user.getIm();
//		String reserved1 = user.getReserved1();
//		String reserved2 = user.getReserved2();
		
		// 非空参数校验
		if(StringUtils.isEmpty(name) || StringUtils.isEmpty(password) || StringUtils.isEmpty(phone) 
				|| StringUtils.isEmpty(email) || StringUtils.isEmpty(sex) || null == age || null == type 
				|| StringUtils.isEmpty(status) || StringUtils.isEmpty(im)) {
			logger.info("必要参数为空！");
			return false;
		}
		// 字符及数字大小校验
		if(name.length() > 32 || password.length() > 128 || sex.length() > 4 || age > 200 
				|| phone.length() > 32 || email.length() > 128) {
			logger.info("参数长度超限！");
			return false;
		}
		
		return true;
	}

	/**
	 * 校验用户密码是否匹配
	 * @param pwd
	 */
	public boolean verifyPwd(Integer id, String pwd) {
		
		// 密码解密
		String pwdFromDb = null;
		// 前台密码hash计算
		String pwdFromInput = null;
		try {
			// 获取用户
			UserEntity user = getUserInfoById(id);
			pwdFromDb = user.getPassword();
			pwdFromInput = KeyDigestUtil.getKeyDigest(pwd, config.getDigest_key());
		} catch (Exception e) {
			logger.error("密码校验异常！", e);
			return false;
		}
		
		// 对比
		if(pwdFromDb.equals(pwdFromInput)) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * 修改密码
	 * @param id
	 * @param pwd
	 * @return
	 */
	@CacheEvict(value = "user", key = "#id")
	public Map<String, String> changePwd(Integer id, String pwd) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("result", "0");
		resultMap.put("reason", "密码修改成功！用户id：" + id);
		
		UserEntity user = new UserEntity();
		user.setId(id);
		user.setPassword(CipherUtil.encrypt(KeyDigestUtil.getKeyDigest(pwd, config.getDigest_key()), config.getCrypt_key()));
		
		// 执行入库
		try {
			int updateResult = userDao.updateUser(user);
			if(updateResult != 1) {
				logger.info("向数据库修改密码失败！");
				resultMap.put("result", "2");
				resultMap.put("reason", "向数据库修改密码失败！");
			} else {
				logger.info("成功修改用户：" + user.toString());
			}
		} catch (Exception e) {
			logger.error("向数据库修改密码失败！用户id：" + id, e);
			resultMap.put("result", "2");
			resultMap.put("reason", "向数据库修改密码失败！");
		}
		
		return resultMap;
	}

	/**
	 * 根据用户id获取登录信息
	 * @param id
	 * @return
	 */
	public UserLoginEntity getLoginInfoById(Integer id) {
		
		UserLoginEntity loginInfo = null;
		try {
			loginInfo = userLoginDao.queryById(id);
			// 结果处理
			if(null != loginInfo){
				// 时间
				if(null == loginInfo.getLoginTime()){
					loginInfo.setLoginTime(new Date());
				}
				if(null == loginInfo.getLastLoginTime()){
					loginInfo.setLastLoginTime(new Date());;
				}
				// IP
				if(StringUtils.isEmpty(loginInfo.getLoginIp())){
					loginInfo.setLoginIp("未知");
				}
				if(StringUtils.isEmpty(loginInfo.getLastLoginIp())){
					loginInfo.setLastLoginIp("未知");
				}
				// 地点
				if(StringUtils.isEmpty(loginInfo.getLoginAddress())){
					loginInfo.setLoginAddress("未知");
				}
				if(StringUtils.isEmpty(loginInfo.getLastLoginAddress())){
					loginInfo.setLastLoginAddress("未知");
				}
			}
			
		} catch (Exception e) {
			logger.error("根据用户id获取登录信息异常！", e);
		}
		return loginInfo;
	}



}
