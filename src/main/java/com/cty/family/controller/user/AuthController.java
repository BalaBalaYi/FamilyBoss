package com.cty.family.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cty.family.entity.AuthEntity;
import com.cty.family.service.AuthService;

/**
 * 权限控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/user/auth/*")
public class AuthController {
	
	private Logger logger = LoggerFactory.getLogger(AuthController.class);
	
	@Autowired
	private AuthService authService;

	/**
	 * 跳转至权限管理主页面
	 * @return
	 */
	@RequestMapping("/toAuth.do")
	public String toAuth() {
		return "/user/auth/auth";
	}
	
	/**
	 * 查询全部权限
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/queryAll.do")
	public Map<String, Object> queryAll() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<AuthEntity> authList = authService.getAllAuthInfo();
		resultMap.put("authList", authList);
		return resultMap;
	}
	

}
