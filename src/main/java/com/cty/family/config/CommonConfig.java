package com.cty.family.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 通用配置
 * @author 陈天熠
 *
 */
@Configuration
public class CommonConfig {

	// 加密解密key
	@Value("${common.crypt_key}")
	private String crypt_key;
	
	// 摘要key
	@Value("${common.digest_key}")
	private String digest_key;

	public String getCrypt_key() {
		return crypt_key;
	}

	public String getDigest_key() {
		return digest_key;
	}
	
}
