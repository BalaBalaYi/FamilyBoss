package com.cty.family.socket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 注入ServerEndpointExporter
 * 
 * desc:这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
 * 
 * @author 陈天熠
 *
 */
@Configuration
public class WebSocketConfig {
	
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
	
}
