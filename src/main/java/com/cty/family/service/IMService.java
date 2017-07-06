package com.cty.family.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.cty.family.entity.GroupEntity;
import com.cty.family.entity.UserEntity;

import net.sf.json.JSONObject;

/**
 * IM业务类
 * @author 陈天熠
 *
 */
@Service
public class IMService {

	private Logger logger = LoggerFactory.getLogger(IMService.class);
	
	@Autowired
	private GroupService groupService;
	@Autowired
	private UserService userService;
	
	/**
	 * IM信息初始化，对应 init接口
	 * @param 当前用户id
	 * @return
	 */
	public JSONObject init(Integer id){
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", 0);
		resultMap.put("msg", "");
		
		/*
		 * data:分为mine,friend,group三个部分
		 */
		Map<String, Object> dataMap = new HashMap<String, Object>();
		
		try{
			// ============== mine:当前用户信息 ==============
			Map<String, Object> mine = new HashMap<String, Object>();
			UserEntity user = userService.getUserInfoById(id);
			mine.put("id", id + "");
			mine.put("username", user.getName());
			mine.put("status", "online");
			mine.put("sign", user.getSign());
			mine.put("avatar", "/file/getImage.do?name=" + (StringUtils.isEmpty(user.getImgName()) ? "default" : user.getImgName()));
			dataMap.put("mine", mine);
			
			// ============== friend:好友信息（即group信息） ==============
			List<Map<String, Object>> friends = new ArrayList<Map<String, Object>>();
			
			// 默认全部用户互为好友
			// 第一部分：获取全部群组信息
			List<GroupEntity> groupsInfo = groupService.getAllGroupsInfo();		
			
			for(GroupEntity groupInfo : groupsInfo){
				Map<String, Object> group = new HashMap<String, Object>();
				group.put("id", groupInfo.getId() + "");
				group.put("groupname", groupInfo.getName());
				// 查询当前组下除当前用户以外的全部用户
				List<Map<String, Object>> userInGroup = new ArrayList<Map<String, Object>>();
				List<UserEntity> groupUserList = userService.getUsersInfoByGroupId(groupInfo.getId());
				for(UserEntity groupUser : groupUserList){
					if(groupUser.getId() == id){ // 好友列表排除当前用户
						continue;
					}
					Map<String, Object> groupUserMap = new HashMap<String, Object>();
					groupUserMap.put("id", groupUser.getId() + "");
					groupUserMap.put("username", groupUser.getName());
					groupUserMap.put("sign", groupUser.getSign());
					// 查询登录信息表以获得该用户的登录状态
					String status = userService.getLoginStatus(groupUser.getId());
					if(null != status && status.equals("ON")){
						groupUserMap.put("status", "online");
					} else {
						groupUserMap.put("status", "offline");
					}
					groupUserMap.put("avatar", "/file/getImage.do?name=" + (StringUtils.isEmpty(groupUser.getImgName()) ? "default" : groupUser.getImgName()));
					userInGroup.add(groupUserMap);
				}
				group.put("list", userInGroup);
				friends.add(group);
			}
			
			// 第二部分：无阻成员（自动归为“默认组”）
			Map<String, Object> group = new HashMap<String, Object>();
			group.put("id", "0");
			group.put("groupname", "默认");
			// 查询没有群组的成员
			List<Map<String, Object>> userInDefaultGroup = new ArrayList<Map<String, Object>>();
			List<UserEntity> defaultGroupUserList = userService.getUsersInfoWithOutGroup();
			for(UserEntity defaultGroupUser : defaultGroupUserList){
				if(defaultGroupUser.getId() == id){ // 好友列表排除当前用户
					continue;
				}
				Map<String, Object> groupUserMap = new HashMap<String, Object>();
				groupUserMap.put("id", defaultGroupUser.getId() + "");
				groupUserMap.put("username", defaultGroupUser.getName());
				groupUserMap.put("sign", defaultGroupUser.getSign());
				// 查询登录信息表以获得该用户的登录状态
				String status = userService.getLoginStatus(defaultGroupUser.getId());
				if(null != status && status.equals("ON")){
					groupUserMap.put("status", "online");
				} else {
					groupUserMap.put("status", "offline");
				}
				groupUserMap.put("avatar", "/file/getImage.do?name=" + (StringUtils.isEmpty(defaultGroupUser.getImgName()) ? "default" : defaultGroupUser.getImgName()));
				userInDefaultGroup.add(groupUserMap);
			}
			group.put("list", userInDefaultGroup);
			friends.add(group);
			
			dataMap.put("friend", friends);
			
			// ============== group:群信息（即chat_group信息） ==============
			List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
			
			// 第一部分：默认有一个全体成员公共频道
			Map<String, Object> allGroup = new HashMap<String, Object>();
			allGroup.put("id", "0");
			allGroup.put("groupname", "全体成员");
			allGroup.put("avatar", "/file/getImage.do?name=default");
			groups.add(allGroup);
			
			// 第二部分：当前用户所在群组
			// 获得该用户所在的群组信息
			List<GroupEntity> userGroupsInfo = groupService.getGroupsInfoByUserId(id);
			for(GroupEntity userGroup : userGroupsInfo){
				Map<String, Object> userSingleGroup = new HashMap<String, Object>();
				userSingleGroup.put("id", userGroup.getId() + "");
				userSingleGroup.put("groupname", userGroup.getName());
				userSingleGroup.put("avatar", "/file/getImage.do?name=" + (StringUtils.isEmpty(userGroup.getImgName()) ? "default" : userGroup.getImgName()));
				groups.add(userSingleGroup);
			}

			dataMap.put("group", groups);
			
			// 合并data
			resultMap.put("data", dataMap);
			
		} catch (Exception e) {
			logger.error("获取IM通讯列表异常！", e);
			resultMap.put("code", 1);
			resultMap.put("msg", "获取IM通讯列表异常");
		}
		
		// map转json
		JSONObject resultJson = null;
		try {
			resultJson = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			logger.error("IM通讯列表格式转换异常！", e);
			resultMap.put("code", 2);
			resultMap.put("msg", "IM通讯列表格式转换异常");
			return null;
		}
		logger.info("IM 初始化信息：" + resultJson.toString());
		return resultJson;
	}

	/**
	 * IM 群组成员信息初始化，对应members接口
	 * @param groupId
	 * @return
	 */
	public JSONObject getMembers(int groupId) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", 0);
		resultMap.put("msg", "");
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		List<Map<String, Object>> groupUserList = new ArrayList<Map<String, Object>>();
		// 获取组内全部用户信息
		List<UserEntity> userList = null;
		if(groupId == 0){ // 如果是默认群组，则获取全部用户
			userList = userService.getAllUsersInfo();
		} else{
			userList = userService.getUsersInfoByGroupId(groupId);
		}
		
		for(UserEntity user : userList){
			Map<String, Object> groupUser = new HashMap<String, Object>();
			groupUser.put("id", user.getId());
			groupUser.put("username", user.getName());
			groupUser.put("sign", user.getSign());
			groupUser.put("avatar", "/file/getImage.do?name=" + (StringUtils.isEmpty(user.getImgName()) ? "default" : user.getImgName()));
			groupUserList.add(groupUser);
		}
		// 添加组内用户信息
		dataMap.put("list", groupUserList);
		// 添加群主信息
		dataMap.put("owner", "");
		
		// 合并data
		resultMap.put("data", dataMap);
		
		// map转json
		JSONObject resultJson = null;
		try {
			resultJson = JSONObject.fromObject(resultMap);
		} catch (Exception e) {
			logger.error("IM群组成员列表格式转换异常！", e);
			resultMap.put("code", 2);
			resultMap.put("msg", "IM群组成员列表格式转换异常");
			return null;
		}
		return resultJson;
	}
}
