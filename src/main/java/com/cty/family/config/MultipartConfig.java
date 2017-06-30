//package com.cty.family.config;
//
//import javax.servlet.MultipartConfigElement;
//
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MultipartConfig {
//
//	/**
//	 * 文件上传临时路径
//	 */
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		factory.setLocation("/static");
//		return factory.createMultipartConfig();
//	}
//}
