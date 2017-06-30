package com.cty.family.controller.main;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * 首页跳转
 * @author 陈天熠
 *
 */
@Controller
public class IndexController {
	
	// 设定首页重定向
	@RequestMapping("/")
	public String index() {
		return "/login";
	}
	
	// 中继跳转至登录页
	@RequestMapping("/toLogin.do")
	public String toLogin(HttpSession session) {
		session.invalidate();
		return "/reLogin";
	}
	
	@RequestMapping("/test")
	public String test(HttpServletRequest request) {
		return "/test";
	}
}
