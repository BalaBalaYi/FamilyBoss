package com.cty.family.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cty.family.entity.UserEntity;
import com.cty.family.service.IMService;

import net.sf.json.JSONObject;

/**
 * IM控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/im/*")
public class IMController {

	@Autowired
	private IMService imService;
	
	@ResponseBody
	@RequestMapping("/init.do")
	public JSONObject init(HttpSession session){
		UserEntity user = (UserEntity) session.getAttribute("userInfo");
		return imService.init(user.getId());
	}
	
	@ResponseBody
	@RequestMapping("/getMembers.do")
	public JSONObject getMembers(@RequestParam("id") String groupId, HttpServletRequest request){
			return imService.getMembers(Integer.parseInt(groupId));
	}
}
