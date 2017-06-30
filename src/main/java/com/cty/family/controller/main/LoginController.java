package com.cty.family.controller.main;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cty.family.entity.UserEntity;
import com.cty.family.entity.UserLoginEntity;
import com.cty.family.service.UserService;
import com.cty.family.util.NetUtil;

@Controller
@RequestMapping("/login/*")
public class LoginController {
	
	private Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/toLogin.do")
	public ModelAndView toLogin(HttpServletRequest request) {
		String msg = request.getParameter("msg");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(null != msg) {
			resultMap.put("reason", "会话已过期，请重新登录！");
		}
		return new ModelAndView("/login", resultMap);
	}
	
	@RequestMapping("/doLogin.do")
	public ModelAndView doLogin(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		String ip = NetUtil.getRemoteHost(request);
		
		Map<String, Object> resultMap = userService.login(name, password, ip);
		String result = (String) resultMap.get("result");
		if("0".equals(result)) {
			logger.info("用户：" + name + " 登录成功！");
			// 登录成功的用户加入session
			UserEntity user = (UserEntity) resultMap.get("user");
			session.setMaxInactiveInterval(30 * 60); // session默认超时时间：半小时
			session.setAttribute("userInfo", user);
			
			// 获取登录信息
			UserLoginEntity loginInfo = userService.getLoginInfoById(user.getId());
			session.setAttribute("loginInfo", loginInfo);
			
			return new ModelAndView("/index", resultMap);
		} else {
			logger.info("用户：" + name + " 登录失败！");
			return new ModelAndView("/login", resultMap);
		}
		
	}
	
	@RequestMapping("/toIndex.do")
	public String toIndex() {	
		return "/main";
	}
	
	@RequestMapping("/doLogout.do")
	public String doLogout(HttpServletRequest request, HttpSession session) {
		UserEntity user = (UserEntity) session.getAttribute("userInfo");
		if(null != user) {
			
			// 更新登录信息
			boolean updateLoginInfoResult = userService.modifyLoginInfo(user, NetUtil.getRemoteHost(request), 1);
			if(!updateLoginInfoResult){
				logger.error("用户登出，更新用户登录信息失败！");
			}
			
			// 登出清除session
			session.invalidate();
			logger.info("用户：" + user.getName() + " 登出成功！");
		}
		return "redirect:toLogin.do";
	}
}
