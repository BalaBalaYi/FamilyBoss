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

import com.cty.family.entity.GroupEntity;
import com.cty.family.entity.UserEntity;
import com.cty.family.service.GroupService;
import com.cty.family.service.UserService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 群组控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/user/group/*")
public class GroupController {
	
	private Logger logger = LoggerFactory.getLogger(GroupController.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private GroupService groupService;

	/**
	 * 跳转至群组管理主页面
	 * @return
	 */
	@RequestMapping("/toGroup.do")
	public String toGroup() {
		return "/user/group/group";
	}
	
	/**
	 * 查询全部群组
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAll.do")
	public Map<String, Object> queryAll() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<GroupEntity> groupList = groupService.getAllGroupsInfo();
		resultMap.put("groupList", groupList);
		return resultMap;
	}
	
	/**
	 * 跳转至群组添加页面
	 * @return
	 */
	@RequestMapping("/toAddGroup.do")
	public String toAddGroup() {
		return "/user/group/addGroup";
	}
	
	/**
	 * 执行群组添加
	 * @param groupStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doAddGroup.do", method = RequestMethod.POST)
	public Map<String, Object> doAddGroup(@RequestBody String groupStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == groupStr) {
			resultMap.put("addResult", "添加失败！");
			return resultMap;
		}
		JSONObject jsonObject = JSONObject.fromObject(groupStr);
		GroupEntity group = (GroupEntity) JSONObject.toBean(jsonObject, GroupEntity.class);
		
		// 头像名称获取并绑定群组
		String imageName = (String) session.getAttribute("group_add_image");
		group.setImgName(imageName);
		
		logger.debug("准备添加群组：" + group.toString());
		Map<String, String> addResult = groupService.addGroup(group);
		session.removeAttribute("group_add_image");
		
		if("0".equals(addResult.get("result"))){
			resultMap.put("addResult", "添加成功！");
		} else {
			resultMap.put("addResult", "添加失败！");
		}
		return resultMap;

	}
	
	/**
	 * 跳转至群组拓扑显示页面
	 * @return
	 */
	@RequestMapping("/toGroupTopology.do")
	public ModelAndView toGroupTopology() {
		
		// 查询所有群组信息
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<GroupEntity> groupList = groupService.getAllGroupsInfo();
		resultMap.put("groupList", groupList);

		return new ModelAndView("/user/group/groupTopology", resultMap);
	}
	
	/**
	 * 跳转至群组修改页面
	 * @param id
	 * @return
	 */
	@RequestMapping("/toUpdateGroup.do")
	public ModelAndView toUpdateGroup(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据参数获取单一群组信息
		GroupEntity group = groupService.getGroupInfoById(id);
		resultMap.put("group", group);
		
		return new ModelAndView("/user/group/updateGroup", resultMap);
	}
	
	/**
	 * 执行群组修改
	 * @param groupStr
	 * @param session
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doUpdateGroup.do", method = RequestMethod.POST)
	public Map<String, Object> doUpdateGroup(@RequestBody String groupStr, HttpSession session) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == groupStr) {
			resultMap.put("updateResult", "修改失败！");
			return resultMap;
		}
		JSONObject jsonObject = JSONObject.fromObject(groupStr);
		GroupEntity group = (GroupEntity) JSONObject.toBean(jsonObject, GroupEntity.class);
		
		// 头像名称获取并绑定群组
		String imageName = (String) session.getAttribute("group_add_image");
		group.setImgName(imageName);
		
		logger.debug("准备修改群组：" + group.toString());
		Map<String, String> updateResult =  groupService.updateGroup(group);
		session.removeAttribute("group_add_image");
		
		if("0".equals(updateResult.get("result"))){
			resultMap.put("updateResult", "修改成功！");
		} else {
			resultMap.put("updateResult", "修改失败！");
		}
		return resultMap;
	}
	
	/**
	 * 跳转至群组配置页面
	 * @param id
	 * @param name
	 * @return
	 */
	@RequestMapping("/toModifyGroup.do")
	public ModelAndView toModifyGroup(@RequestParam("id") Integer id, @RequestParam("name") String name) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 查询全体用户
		List<UserEntity> allUserList = userService.getAllUsersIdAndName();
		// 查询目前已在组中成员
		List<UserEntity> groupUserList = userService.getAllUsersIdAndNameByGroupId(id);
		// 筛选
		List<Map<String, Object>> filterList = groupService.filterGroupMember(allUserList, groupUserList);
		resultMap.put("filterList", filterList);
		resultMap.put("id", id);
		resultMap.put("name", name);
		
		return new ModelAndView("/user/group/modifyGroup", resultMap);
	}
	
	/**
	 * 执行群组配置
	 * @param dataStr
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/doModifyGroup.do", method = RequestMethod.POST)
	public Map<String, Object> doModifyGroup(@RequestBody String dataStr, HttpServletRequest request) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 获取表单信息，json字符串转换为json对象  
		if(null == dataStr) {
			resultMap.put("modifyResult", "修改失败！");
			return resultMap;
		}
		JSONObject jsonObject = JSONObject.fromObject(dataStr);
		Integer groupId = Integer.parseInt((String) jsonObject.get("groupId"));
		JSONArray jsonArray = (JSONArray) jsonObject.get("data");
		
		List<Integer> idList = new ArrayList<Integer>();
		Iterator<String> it = jsonArray.iterator();
		while (it.hasNext()) {
			idList.add(Integer.parseInt(it.next()));
		}
	
		Map<String, String> modifyResult =  groupService.modifyGroup(groupId, idList);

		if("0".equals(modifyResult.get("result"))){
			resultMap.put("modifyResult", "配置成功！");
		} else {
			resultMap.put("modifyResult", "配置失败！");
		}
		return resultMap;
	}
	
	/**
	 * 执行群组删除
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/doDeleteGroup.do")
	public Map<String, Object> doDeleteGroup(@RequestParam("id") Integer id) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		logger.debug("准备删除群组，id：" + id);
		Map<String, String> deleteResult =  groupService.deleteGroup(id);
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
