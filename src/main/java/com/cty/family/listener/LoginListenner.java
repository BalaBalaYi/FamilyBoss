package com.cty.family.listener;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cty.family.entity.UserEntity;


@WebListener
public class LoginListenner implements HttpSessionAttributeListener {
	
	private Logger logger = LoggerFactory.getLogger(LoginListenner.class);

	/** 
	 * 用于存放用户账号和session对应关系的map
	 */ 
	private Map<Integer, HttpSession> map = new HashMap<Integer, HttpSession>();

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		
		String name = event.getName();
		
		if (name.equals("userInfo")) {
			UserEntity user = (UserEntity) event.getValue();
			if (map.get(user.getId()) != null) {
				HttpSession session = map.get(user.getId());
				session.removeAttribute("userInfo");
//				session.invalidate();
				logger.info("清除重复登录用户的session信息");
			}
			map.put(user.getId(), event.getSession());
		}
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		
		String name = event.getName();
		
		if (name.equals("userInfo")) {
			map.remove("userInfo");
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		// TODO Auto-generated method stub
		
	}

	// get/set 方法
	public Map<Integer, HttpSession> getMap() {
		return map;
	}

	public void setMap(Map<Integer, HttpSession> map) {
		this.map = map;
	}

}
