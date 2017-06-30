package com.cty.family.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/extra/*")
public class ExtraController {

	@RequestMapping("showPlanet.do")
	public String showPlanet() {
		return "/extra/planet";
	}
	
	@RequestMapping("showCalculator.do")
	public String showCalculator() {
		return "/extra/calculator";
	}
}
