package com.cty.family.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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

import com.cty.family.entity.AuthEntity;
import com.cty.family.entity.RoleEntity;
import com.cty.family.service.RoleService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 角色控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/user/role/*")
public class RoleController {
	
	private Logger logger = LoggerFactory.getLogger(RoleController.class);
	
	@Autowired
	private RoleService roleService;

	/**
	 * 跳转至角色管理主页面
	 * @return
	 */
	@RequestMapping("/toRole.do")
	public String toRole() {
		return "/user/role/role";
	}
	
	/**
	 * 查询全部角色
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAll.do")
	public Map<String, Object> queryAll() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<RoleEntity> roleList = roleService.getAllRoleInfo();
		
		// 组装返回数据
		if(null == roleList){
			resultMap.put("code", 1);
			resultMap.put("msg", "查询失败！");
			resultMap.put("count", 0);
			resultMap.put("data", null);
		} else {
			resultMap.put("code", 0);
			resultMap.put("msg", "查询成功！");
			resultMap.put("count", roleList.size());
			resultMap.put("data", roleList);
		}
		
		return resultMap;
	}
	
	/**
	 * 跳转至角色添加页面
	 * @return
	 */
	@RequestMapping("/toAddRole.do")
	public String toAddRole() {
		return "/user/role/addRole";
	}
	
	/**
	 * 执行角色添加
	 * @param roleStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doAddRole.do", method = RequestMethod.POST)
	public Map<String, Object> doAddRole(@RequestBody String roleStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == roleStr) {
			resultMap.put("addResult", "添加失败！");
			return resultMap;
		}
		JSONObject jsonObject = JSONObject.fromObject(roleStr);
		RoleEntity role = (RoleEntity) JSONObject.toBean(jsonObject, RoleEntity.class);
		
		logger.debug("准备添加角色：" + role.toString());
		Map<String, String> addResult = roleService.addRole(role);
		
		if("0".equals(addResult.get("result"))){
			resultMap.put("addResult", "添加成功！");
		} else {
			resultMap.put("addResult", "添加失败！");
		}
		return resultMap;

	}
	
	/**
	 * 跳转至角色修改页面
	 * @param id
	 * @return
	 */
	@RequestMapping("/toUpdateRole.do")
	public ModelAndView toUpdateRole(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据参数获取单一角色信息
		RoleEntity role = roleService.getRoleInfoById(id);
		resultMap.put("role", role);
		
		return new ModelAndView("/user/role/updateRole", resultMap);
	}
	
	/**
	 * 执行角色修改
	 * @param roleStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doUpdateRole.do", method = RequestMethod.POST)
	public Map<String, Object> doUpdateRole(@RequestBody String roleStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == roleStr) {
			resultMap.put("updateResult", "修改失败！");
			return resultMap;
		}
		JSONObject jsonObject = JSONObject.fromObject(roleStr);
		RoleEntity role = (RoleEntity) JSONObject.toBean(jsonObject, RoleEntity.class);
		
		logger.debug("准备修改角色：" + role.toString());
		Map<String, String> updateResult =  roleService.updateRole(role);
		
		if("0".equals(updateResult.get("result"))){
			resultMap.put("updateResult", "修改成功！");
		} else {
			resultMap.put("updateResult", "修改失败！");
		}
		return resultMap;
	}
	
	/**
	 * 跳转至角色配置页面
	 * @param id
	 * @param name
	 * @return
	 */
//	@RequestMapping("/toModifyRole.do")
//	public ModelAndView toModifyRole(@RequestParam("id") Integer id, @RequestParam("name") String name) {
//		
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		
//		// 查询全体权限
//		List<AuthEntity> allAuthList = authService.getAllUsersIdAndName();
//		// 查询目前已在角色中的权限
//		List<AuthEntity> roleAuthList = authService.getAllUsersIdAndNameByGroupId(id);
//		// 筛选
//		List<Map<String, Object>> filterList = authService.filterRoleMember(allUserList, groupUserList);
//		resultMap.put("filterList", filterList);
//		resultMap.put("id", id);
//		resultMap.put("name", name);
//		
//		return new ModelAndView("/user/group/modifyGroup", resultMap);
//	}
	
	/**
	 * 执行角色配置
	 * @param dataStr
	 * @param request
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping(value = "/doModifyRole.do", method = RequestMethod.POST)
//	public Map<String, Object> doModifyRole(@RequestBody String dataStr, HttpServletRequest request) {
//
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		
//		// 获取表单信息，json字符串转换为json对象  
//		if(null == dataStr) {
//			resultMap.put("modifyResult", "修改失败！");
//			return resultMap;
//		}
//		JSONObject jsonObject = JSONObject.fromObject(dataStr);
//		Integer groupId = Integer.parseInt((String) jsonObject.get("groupId"));
//		JSONArray jsonArray = (JSONArray) jsonObject.get("data");
//		
//		List<Integer> idList = new ArrayList<Integer>();
//		Iterator<String> it = jsonArray.iterator();
//		while (it.hasNext()) {
//			idList.add(Integer.parseInt(it.next()));
//		}
//	
//		Map<String, String> modifyResult =  groupService.modifyGroup(groupId, idList);
//
//		if("0".equals(modifyResult.get("result"))){
//			resultMap.put("modifyResult", "配置成功！");
//		} else {
//			resultMap.put("modifyResult", "配置失败！");
//		}
//		return resultMap;
//	}
	
	/**
	 * 执行角色删除
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDeleteRole.do")
	public Map<String, Object> doDeleteRole(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		logger.debug("准备删除角色，id：" + id);
		Map<String, String> deleteResult =  roleService.deleteRole(id);
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
}
