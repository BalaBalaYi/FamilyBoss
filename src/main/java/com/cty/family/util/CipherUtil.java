package com.cty.family.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.sound.sampled.AudioFormat.Encoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @ClassName: CipherUtil
 * @Description: 加密解密工具类
 * @author chenty
 */
public class CipherUtil {
	
	private static Logger logger = LoggerFactory.getLogger(CipherUtil.class);
	
	//对称加密方式
	public static final String AES = "AES";
	public static final String DES = "DES";
	public static final String TRI_DES = "DESede";//默认值
	
	//非对称加密方式
	public static final String RSA = "RSA";
	
	public static final String charSetUTF8 = "UTF-8";//默认值
	public static final String charSetGBK = "GBK";
	public static final String charSet88591 = "ISO-8859-1";
	
	/**
	 * 加密
	 * @param encryptContent	待加密内容字符串
	 * @param encryptKey		加密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param encryptType		加密方式:TRI_DES
	 * @param charset			编码方式:UTF-8
	 * @return
	 */
	public static String encrypt(String encryptContent, Object encryptKey){
		return encryptWithTypeAndCharset(encryptContent, encryptKey, TRI_DES, charSetUTF8);
	}
	
	/**
	 * 加密
	 * @param encryptContent	待加密内容字符串
	 * @param encryptKey		加密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param encryptType		加密方式
	 * @param charset			编码方式:UTF-8
	 * @return
	 */
	public static String encryptWithType(String encryptContent, Object encryptKey, String encryptType){
		return encryptWithTypeAndCharset(encryptContent, encryptKey, encryptType, charSetUTF8);
	}
	
	/**
	 * 加密
	 * @param encryptContent	待加密内容字符串
	 * @param encryptKey		加密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param encryptType		加密方式
	 * @param charset			编码方式
	 * @return
	 */
	public static String encryptWithTypeAndCharset(String encryptContent, Object encryptKey, String encryptType, String charset) {
		
		Key key = null;
		//对称加密方式获得key
		if (!encryptType.equals(RSA)) {
			if (encryptKey instanceof String) {
				key = new SecretKeySpec(getKey(encryptKey, encryptType), encryptType);
			} else if (encryptKey instanceof Key) {
				key = (Key) encryptKey;
			} else {
				logger.error("对称加密方式传入秘钥参数类型不正确，encryptKey参数值：" + encryptKey.toString() + "，encryptKey参数类型：" + encryptKey.getClass());
				return null;
			}
		}
		//非对称加密方式获得key
		else{
			if (encryptKey instanceof String) {
				//获得私钥
				key = CipherUtil.getPrivateKey((String) encryptKey);
				//获得公钥
//				key = CipherUtil.getPublicKey((String) encryptKey);
			} else if (encryptKey instanceof Key){
				key = (Key) encryptKey;
			} else {
				logger.error("非对称加密方式传入秘钥参数类型不正确，encryptKey参数值：" + encryptKey.toString() + "，encryptKey参数类型：" + encryptKey.getClass());
				return null;
			}
			
		}
		
		Cipher cipher;
		String encryptResult = null;
		try {
			cipher = Cipher.getInstance(encryptType);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encryptResult = byteArr2HexStr(cipher.doFinal(encryptContent.getBytes(charset)));
		} catch (Exception e) {
			logger.error(encryptType + "加密发生异常！加密内容：" + encryptContent + "，加密key：" + encryptKey + "，加密类型：" + encryptType + "，编码方式：" + charset, e);
		}
		return encryptResult;
	}
	
	/**
	 * 解密
	 * @param decryptContent	待解密内容字符串
	 * @param decryptKey		解密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param decryptType		解密方式:TRI_DES
	 * @param charset			编码方式:UTF-8
	 * @return
	 */
	public static String decrypt(String decryptContent, Object decryptKey) {
		return decryptWithTypeAndCharset(decryptContent, decryptKey, TRI_DES, charSetUTF8);
	}
	
	/**
	 * 解密
	 * @param decryptContent	待解密内容字符串
	 * @param decryptKey		解密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param decryptType		解密方式
	 * @param charset			编码方式:UTF-8
	 * @return
	 */
	public static String decryptWithType(String decryptContent, Object decryptKey, String decryptType) {
		return decryptWithTypeAndCharset(decryptContent, decryptKey, decryptType, charSetUTF8);
	}
	
	/**
	 * 解密
	 * @param decryptContent	待解密内容字符串
	 * @param decryptKey		解密key（RSA的key必须为BASE64编码的字符串形式或是Key类）
	 * @param decryptType		解密方式
	 * @param charset			编码方式
	 * @return
	 */
	public static String decryptWithTypeAndCharset(String decryptContent, Object decryptKey, String decryptType, String charset) {
		
		Key key = null;
		//对称加密方式获得key
		if (!decryptType.equals(RSA)) {
			if (decryptKey instanceof String) {
				key = new SecretKeySpec(getKey(decryptKey, decryptType), decryptType);
			} else if (decryptKey instanceof Key) {
				key = (Key) decryptKey;
			} else {
				logger.error("对称解密方式传入秘钥参数类型不正确，decryptKey参数值：" + decryptKey.toString() + "，decryptKey参数类型：" + decryptKey.getClass());
				return null;
			}
		}
		//非对称加密方式获得key
		else{
			if (decryptKey instanceof String) {
				//获得私钥
				key = CipherUtil.getPrivateKey((String) decryptKey);
				//获得公钥
//				key = CipherUtil.getPublicKey((String) encryptKey);
			} else if (decryptKey instanceof Key){
				key = (Key) decryptKey;
			} else {
				logger.error("非对称解密方式传入秘钥参数类型不正确，decryptKey参数值：" + decryptKey.toString() + "，decryptKey参数类型：" + decryptKey.getClass());
				return null;
			}
			
		}
		
		Cipher cipher;
		String decryptResult = null;
		try {
			cipher = Cipher.getInstance(decryptType);
			cipher.init(Cipher.DECRYPT_MODE, key);
			decryptResult = new String(cipher.doFinal(hexStr2ByteArr(decryptContent)), charset);
		} catch (Exception e) {
			logger.error(decryptType + "解密发生异常！解密内容：" + decryptContent + "，解密key：" + decryptKey + "，解密类型：" + decryptType + "，编码方式：" + charset);
			return null;
		}
		return decryptResult;
	}





	/**
	 * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
	 * hexStr2ByteArr(String strIn) 互为可逆的转换过程
	 * 
	 * @param arrB	需要转换的byte数组
	 * @return		转换后的字符串
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 */
	public static String byteArr2HexStr(byte[] arrB) throws Exception {
		int iLen = arrB.length;
		// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
		StringBuffer sb = new StringBuffer(iLen * 2);
		for (int i = 0; i < iLen; i++) {
			int intTmp = arrB[i];
			// 把负数转换为正数
			while (intTmp < 0) {
				intTmp = intTmp + 256;
			}
			// 小于0F的数需要在前面补0
			if (intTmp < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(intTmp, 16));
		}
		return sb.toString();
	}

	/**
	 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
	 * 互为可逆的转换过程
	 * 
	 * @param strIn		需要转换的字符串
	 * @return			转换后的byte数组
	 * @throws Exception	本方法不处理任何异常，所有异常全部抛出
	 *
	 */
	public static byte[] hexStr2ByteArr(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}
	
	/**
	 * 加密解密前的参数key的处理
	 * @param key		加密或解密key
	 * @param type		加密或解密类型
	 * @return			byte[] key
	 */
	public static byte[] getKey(Object key, String type){
		
		byte[] arrayOut = null;
		
		if(AES.equals(type)){
			
			if(key instanceof String){
				// 创建一个空的16字节数组（默认值为0）
				arrayOut = new byte[16];
				
				// 将原始字节数组转换为16字节
				byte[] arrayIn = ((String) key).getBytes();
				for (int i = 0; i < arrayIn.length && i < arrayOut.length; i++) {
					arrayOut[i] = arrayIn[i];
				}
			}
		} else if (DES.equals(type)){
			if(key instanceof String){
				// 创建一个空的8字节数组（默认值为0）
				arrayOut = new byte[8];
				
				// 将原始字节数组转换为8字节
				byte[] arrayIn = ((String) key).getBytes();
				for (int i = 0; i < arrayIn.length && i < arrayOut.length; i++) {
					arrayOut[i] = arrayIn[i];
				}
			}
		} else if (TRI_DES.equals(type)){
			if(key instanceof String){
				// 创建一个空的24字节数组（默认值为0）
				arrayOut = new byte[24];
				
				// 将原始字节数组转换为24字节
				byte[] arrayIn = ((String) key).getBytes();
				for (int i = 0; i < arrayIn.length && i < arrayOut.length; i++) {
					arrayOut[i] = arrayIn[i];
				}
			}
		} else {
			logger.warn("不支持的对称加密方法！目前支持的对称加密方法：AES,DES,3DES");
		}
		return arrayOut;
	}
	
	/** 
	 * 生成公钥和私钥 （RSA）
	 * @return HashMap<String, Object> 1.key:"public" value:公钥   2.key:"private" value:私钥
	 * @throws NoSuchAlgorithmException  
	 * 
	 */  
	public static HashMap<String, Object> getRSAKeys() {
		HashMap<String, Object> map = new HashMap<String, Object>();  
		try {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");  
			keyPairGen.initialize(1024);  
			KeyPair keyPair = keyPairGen.generateKeyPair();  
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();  
			map.put("public", publicKey);  
			map.put("private", privateKey);
		} catch (Exception e) {
			logger.error("生成公钥和私钥 （RSA）失败", e);
		}  
		return map;  
	}
	
	/**
	 * 得到公钥（RSA）
	 * @param key 密钥字符串（经过base64编码）
	 * @return 
	 */
	public static PublicKey getPublicKey(String key) {
		byte[] keyBytes;
		PublicKey publicKey = null;
		try {
			keyBytes = (new BASE64Decoder()).decodeBuffer(key);
			
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			publicKey = keyFactory.generatePublic(keySpec);
		} catch (Exception e) {
			logger.error("根据字符串key：" + key + " 获得公钥（RSA）失败", e);
		}
		return publicKey;
	}
	
	/**
	 * 得到私钥（RSA）
	 * @param key 密钥字符串（经过base64编码）
	 * @return 
	 */
	public static PrivateKey getPrivateKey(String key) {
		byte[] keyBytes;
		PrivateKey privateKey = null;
		try {
			keyBytes = (new BASE64Decoder()).decodeBuffer(key);
			
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(keySpec);
		} catch (Exception e) {
			logger.error("根据字符串key：" + key + " 获得私钥（RSA）失败", e);
		}
		return privateKey;
	}
	
	/**
	 * 得到密钥字符串（经过base64编码）
	 * @return
	 */
	public static String getKeyString(Key key) {
		String s = null;
		try {
			byte[] keyBytes = key.getEncoded();
			s = (new BASE64Encoder()).encode(keyBytes);
		} catch (Exception e) {
			logger.error("得到密钥字符串（经过base64编码）失败，key：" + key.toString(), e);
		}
		return s;
	}
	
	public static void main(String[] args) {
		
		String result = decrypt("56740ba59e23ea8a304b14cda6f42dff2a5995fed245ff3df6edafbb869c402584542db7ab63a00499933d40fb3bce0d", "123456789");
		System.out.println(result);
	}

}
