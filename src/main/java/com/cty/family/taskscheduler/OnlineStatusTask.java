package com.cty.family.taskscheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cty.family.dao.UserLoginDao;
import com.cty.family.entity.UserLoginEntity;
import com.cty.family.service.UserService;
import com.cty.family.socket.MyWebSocket;

/**
 * 人员在线状态同步作业
 * 
 * 定时通过socket连接检查在线人员，同步数据库信息
 * 
 * @author 陈天熠
 *
 */
@Component
public class OnlineStatusTask {
	
	private Logger logger = LoggerFactory.getLogger(OnlineStatusTask.class);
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserLoginDao userLoginDao;
	
	/**
	 * 同步数据库表人员的登录状态
	 */
	@Scheduled(cron = "0/60 * * ? * *")
	public void syncLoginStatus(){
		
		// 从数据库查找当前在线用户id
		List<UserLoginEntity> onlineList = userLoginDao.queryAllOnline();
		
		for(UserLoginEntity onlineUser : onlineList){
			// 根据socket连接校验该用户是否仍然在线
			if(MyWebSocket.getWebSocket(onlineUser.getUserId() + "") == null) { // 如果不在线，改数据库为下线状态
				userService.modifyLoginInfo(onlineUser.getUserId(), onlineUser.getLoginIp(), 1);
				logger.info("将用户id：" + onlineUser.getUserId() + " 的数据库在线状态改为\"下线\"");
			}
		}
		
	}
}
