package com.cty.family.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cty.family.entity.UserEntity;
import com.cty.family.service.UserService;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * 用户控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/user/*")
public class UserController {
	
	private Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;

	/**
	 * 跳转至用户管理主页面
	 * @return
	 */
	@RequestMapping("/toUser.do")
	public ModelAndView toUser() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		return new ModelAndView("/user/user", resultMap);
	}
	
	/**
	 * 查询全部用户
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAll.do")
	public Map<String, Object> queryAll() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<UserEntity> userList = userService.getAllUsersInfo();
		resultMap.put("userList", userList);
		return resultMap;
	}
	
	/**
	 * 根据用户id查询用户
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryById.do")
	public Map<String, Object> queryById(@RequestParam("id") Integer id) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> userDetail = userService.getUserInfoByIdWithMapRet(id);
		resultMap.put("userDetail", userDetail);
		return resultMap;
	}
	
	/**
	 * 根据组id返回组员（组id为0，则返回全体成员）
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryByGroupId.do")
	public Map<String, Object> queryByGroupId(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<UserEntity> userList = null;
		
		if(0 == id){
			userList = userService.getAllUsersInfo();
		} else {
			userList = userService.getUsersInfoByGroupId(id);
		}
		resultMap.put("groupMember", userList);
		
		return resultMap;
	}
	
	/**
	 * 跳转至添加用户页面
	 * @return
	 */
	@RequestMapping("/toAddUser.do")
	public ModelAndView toAddUser() {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//获取男性和女性列表
		resultMap.put("maleList", userService.getMaleUsers());
		resultMap.put("femaleList", userService.getFemaleUsers());
		
		return new ModelAndView("/user/addUser", resultMap);
	}
	
	/**
	 * 执行用户添加
	 * @param userStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doAddUser.do", method = RequestMethod.POST)
	public Map<String, Object> doAddUser(@RequestBody String userStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == userStr) {
			resultMap.put("addResult", "添加失败！");
			return resultMap;
		}
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy/MM/dd", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"}));
		JSONObject jsonObject = JSONObject.fromObject(userStr);
		UserEntity user = (UserEntity) JSONObject.toBean(jsonObject, UserEntity.class);
		
		// 头像名称获取并绑定用户
		String imageName = (String) session.getAttribute("user_add_image");
		user.setImgName(imageName);
		
		logger.debug("准备添加用户：" + user.toString());
		Map<String, String> addResult = userService.addUser(user);
		session.removeAttribute("user_add_image");
		
		if("0".equals(addResult.get("result"))){
			resultMap.put("addResult", "添加成功！");
		} else {
			resultMap.put("addResult", "添加失败！");
		}
		return resultMap;

	}
	
	/**
	 * 跳转至修改密码页面
	 * @return
	 */
	@RequestMapping("/toChangePwd.do")
	public String toChangePwd() {
		return "/user/changePwd";
	}
	
	/**
	 * 密码异步校验
	 * @param pwd
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doPwdVerify.do")
	public Map<String, Object> doPwdVerify(@RequestParam("pwd") String pwd, HttpSession session) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 当前用户id
		UserEntity user = (UserEntity) session.getAttribute("userInfo");
		
		//　校验密码
		boolean result = userService.verifyPwd(user.getId(), pwd);
		resultMap.put("result", result);
		
		return resultMap;
	}
	
	/**
	 * 执行密码修改
	 * @param pwd
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doPwdChange.do")
	public Map<String, Object> doPwdChange(@RequestParam("pwd") String pwd, HttpSession session) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 当前用户id
		UserEntity user = (UserEntity) session.getAttribute("userInfo");
		
		//　修改密码
		Map<String, String> changeResult = userService.changePwd(user.getId(), pwd);
		
		if("0".equals(changeResult.get("result"))){
			resultMap.put("changeResult", "修改成功！");
		} else {
			resultMap.put("changeResult", "修改失败！");
		}
		return resultMap;
	}
	
	/**
	 * 跳转至修改用户页面
	 * @param id
	 * @return
	 */
	@RequestMapping("/toUpdateUser.do")
	public ModelAndView toUpdateUser(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据参数获取单一用户信息
		UserEntity user = userService.getUserInfoById(id);
		resultMap.put("user", user);
		
		//获取男性和女性列表
		resultMap.put("maleList", userService.getMaleUsers());
		resultMap.put("femaleList", userService.getFemaleUsers());
		
		return new ModelAndView("/user/updateUser", resultMap);
	}
	
	/**
	 * 执行用户修改
	 * @param userStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doUpdateUser.do", method = RequestMethod.POST)
	public Map<String, Object> doUpdateUser(@RequestBody String userStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == userStr) {
			resultMap.put("updateResult", "修改失败！");
			return resultMap;
		}
		JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[] {"yyyy/MM/dd", "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss"}));
		JSONObject jsonObject = JSONObject.fromObject(userStr);
		UserEntity user = (UserEntity) JSONObject.toBean(jsonObject, UserEntity.class);
		
		// 头像名称获取并绑定用户
		String imageName = (String) session.getAttribute("user_add_image");
		user.setImgName(imageName);
		
		logger.debug("准备修改用户：" + user.toString());
		Map<String, String> updateResult =  userService.updateUser(user);
		session.removeAttribute("user_add_image");
		
		if("0".equals(updateResult.get("result"))){
			resultMap.put("updateResult", "修改成功！");
		} else {
			resultMap.put("updateResult", "修改失败！");
		}
		return resultMap;
	}
	
	/**
	 * 执行用户删除
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDeleteUser.do")
	public Map<String, Object> doDeleteUser(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		logger.debug("准备删除用户，id：" + id);
		Map<String, String> deleteResult =  userService.deleteUser(id);
		String result = deleteResult.get("result");
		String reason = deleteResult.get("reason");
		
		if("0".equals(result)){
			resultMap.put("deleteResult", "删除成功！");
		} else if ("1".equals(result)) {
			resultMap.put("deleteResult", "删除失败！");
			resultMap.put("reason", reason);
		} else {
			resultMap.put("deleteResult", "删除失败！");
			resultMap.put("reason", reason);
		}
		return resultMap;
	}
	
	/**
	 * 执行用户账户状态的开启和关闭
	 * @param id
	 * @param type
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doStatusOpt.do")
	public Map<String, Object> doStatusOpt(@RequestParam("id") Integer id, @RequestParam("type") Integer type, HttpSession session) {
		
		logger.debug("准备" + (type == 0 ? "关闭" : "开启") + "用户账户，id：" + id);
		if(id == 0 && type == 0) {
			UserEntity user = (UserEntity) session.getAttribute("userInfo");
			id = user.getId();
			logger.debug("准备关闭当前用户账户，id：" + id);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
			
		Map<String, String> doStatusResult =  userService.doStatus(id, type);
		String result = doStatusResult.get("result");
		String reason = doStatusResult.get("reason");
		
		if("0".equals(result)){
			resultMap.put("doStatusResult", (type == 0 ? "关闭账户" : "开启账户") + "成功！");
		} else if ("1".equals(result)) {
			resultMap.put("doStatusResult", (type == 0 ? "关闭账户" : "开启账户") + "失败！");
			resultMap.put("reason", reason);
		} else {
			resultMap.put("doStatusResult", (type == 0 ? "关闭账户" : "开启账户") + "失败！");
			resultMap.put("reason", reason);
		}
		return resultMap;
	}
	
}
