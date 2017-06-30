package com.cty.family.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: KeyDigestUtil
 * @Description: 摘要工具类
 * @author chenty
 */
public class KeyDigestUtil {
	
	private static Logger logger = LoggerFactory.getLogger(KeyDigestUtil.class);

	public static final String MD5 = "MD5";
	public static final String SHA1 = "SHA-1";//默认值
	public static final String SHA256 = "SHA-256";
	public static final String SHA512 = "SHA-512";
	
	public static final String charSetUTF8 = "UTF-8";//默认值
	public static final String charSetGBK = "GBK";
	public static final String charSet88591 = "ISO-8859-1";
	
	
	/**
	 * 根据key获取摘要信息
	 * @param strSrc	待生成摘要的内容字符串
	 * @param key		生成摘要所需key
	 * @param type		生成摘要的方式：SHA-1
	 * @param charset	编码方式：UTF-8
	 * @return
	 */
	public static String getKeyDigest(String strSrc, String key){
		return getKeyDigestWithType(strSrc, key, SHA1);
	}
	
	/**
	 * 根据key获取摘要信息
	 * @param strSrc	待生成摘要的内容字符串
	 * @param key		生成摘要所需key
	 * @param type		生成摘要的方式
	 * @param charset	编码方式：UTF-8
	 * @return
	 */
	public static String getKeyDigestWithType(String strSrc, String key, String type){
		return getKeyDigestWithTypeAndCharset(strSrc, key, type, charSetUTF8);
	}
	
	/**
	 * 根据key获取摘要信息
	 * @param strSrc	待生成摘要的内容字符串
	 * @param key		生成摘要所需key
	 * @param type		生成摘要的方式
	 * @param charset	编码方式
	 * @return
	 */
	public static String getKeyDigestWithTypeAndCharset(String strSrc, String key, String type, String charset) {
		try {
			MessageDigest md = MessageDigest.getInstance(type);
			md.update(strSrc.getBytes(charset));
			
			String result="";
			byte[] temp;
			temp=md.digest(key.getBytes(charset));
			for (int i=0; i<temp.length; i++){
				result+=Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
			}
			return result.toLowerCase();
		}catch(Exception e) {
			logger.error("以" + type + "方式获取摘要发生异常！内容：" + strSrc + "，key:" + key + "，编码方式：" + charset);
			return null;
		}
	}
	
}
