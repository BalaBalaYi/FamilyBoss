package com.cty.family.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cty.family.service.SystemService;

/**
 * 系统管理控制类
 * @author 陈天熠
 *
 */
@Controller
@RequestMapping("/system/*")
public class SystemController {

	@Autowired
	private SystemService systemService;
	
	@RequestMapping("getSystemInfo.do")
	public ModelAndView toSqlMonitor() {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 操作系统信息
		List<Map<String, String>> osInfoList = systemService.getOsInfo();
		resultMap.put("osInfoList", osInfoList);
		
		// 网络信息
		List<Map<String, String>> netInfoList = systemService.getNetInfo();
		resultMap.put("netInfoList", netInfoList);
		
		// 应用信息
		List<Map<String, String>> appInfoList = systemService.getAppInfo();
		resultMap.put("appInfoList", appInfoList);
		
		
		return new ModelAndView("/system/info", resultMap);
	}
	
	
}
