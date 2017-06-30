package com.cty.family.controller.common;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import com.cty.family.config.CommonConfig;
import com.cty.family.entity.ImageEntity;
import com.cty.family.enums.FileType;
import com.cty.family.service.ImageService;
import com.cty.family.util.KeyDigestUtil;

import eu.bitwalker.useragentutils.UserAgent;

/**
 * 文件上传下载控制类
 * 
 * 头像：因考虑到经常异步读取且用户和群组所涉及图片数据较少，因此采用保存至数据库的方式（单个图片）
 * 其它：分为图像类型文件和非图像类型文件——主要区别在于，前者返回显示链接，后者返回下载链接
 * 
 * @author 陈天熠
 *
 */
@ControllerAdvice
@RequestMapping("/file/*")
public class FileController {

	private Logger logger = LoggerFactory.getLogger(FileController.class);
	
	public static final String UPLOAD_ROOT = "D:\\workspace\\cty\\FamilyBoss\\src\\main\\resources\\public\\upload\\";

	private final ResourceLoader resourceLoader;
	
	@Autowired
	public FileController(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@Autowired
	private ImageService imageService;
	@Autowired
	private CommonConfig config;
	
	/**
	 * 头像图片base64上传
	 * @param data 图片数据base64编码
	 * @param type user,group 用于区分是用户头像还是群组头像
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/imageUploadBase64.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> imageUploadBase64(@RequestParam("data") String data, @RequestParam("type") String type, HttpSession session) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", "0");
		resultMap.put("reason", "上传成功！");
		
		try{
			String imgDataPrix = "";
			String imgData = "";

			if(data == null || "".equals(data)){
				resultMap.put("result", "1");
				resultMap.put("reason", "图片数据不合法！");
				return resultMap;
			} else {
				String [] d = data.split("base64,");
				if(d != null && d.length == 2){
					imgDataPrix = d[0];
					imgData = d[1];
				} else {
					resultMap.put("result", "1");
					resultMap.put("reason", "图片数据不合法！");
					return resultMap;
				}
			}

			logger.debug("对数据进行解析，获取文件名和流数据");
			String suffix = "";
			if("data:image/jpeg;".equalsIgnoreCase(imgDataPrix)){//data:image/jpeg;base64,base64编码的jpeg图片数据
				suffix = ".jpg";
			} else if ("data:image/x-icon;".equalsIgnoreCase(imgDataPrix)){//data:image/x-icon;base64,base64编码的icon图片数据
				suffix = ".ico";
			} else if ("data:image/gif;".equalsIgnoreCase(imgDataPrix)){//data:image/gif;base64,base64编码的gif图片数据
				suffix = ".gif";
			} else if ("data:image/png;".equalsIgnoreCase(imgDataPrix)){//data:image/png;base64,base64编码的png图片数据
				suffix = ".png";
			} else {
				resultMap.put("result", "1");
				resultMap.put("reason", "图片数据不合法！");
				return resultMap;
			}
			String tempFileName = "upload_" + suffix;
			logger.debug("生成文件名为："+tempFileName);
			
			// 此处使用spring框架提供的base64工具包
			byte[] bs = Base64Utils.decodeFromString(imgData);
			
			// 入库
			ImageEntity image = new ImageEntity();
			image.setName(KeyDigestUtil.getKeyDigest(DatatypeConverter.printBase64Binary(bs), config.getDigest_key())); // 对文件计算hash值
			image.setContent(bs);
			boolean saveResult = imageService.saveImage(image);
			if(!saveResult){
				resultMap.put("result", "2");
				resultMap.put("reason", "入库失败！");
				return resultMap;
			} else {
				// 将图片名称加入session
				session.setAttribute(type + "_add_image", image.getName());
			}
		}catch (Exception e) {
			logger.error("上传失败!", e);
			resultMap.put("result", "3");
			resultMap.put("reason", "未知错误！");
		}
	return resultMap;
	}
	
	/**
	 * 从数据库获取头像图片文件（仅为头像图片提供功能）
	 * @param name
	 * @param response
	 */
	@RequestMapping(value="/getImage.do",method = RequestMethod.GET)
	public void getImage(@PathParam("name")String name, HttpServletResponse response){
		
		if(null == name || "" == name){
			name = "default";
		}
		
		// 从数据库获取图像信息
		ImageEntity image = imageService.getImageByName(name);
		byte[] data = image.getContent();
		
		response.setContentType("img/jpeg");
		response.setCharacterEncoding("utf-8");
		try {
			OutputStream outputStream=response.getOutputStream();
			InputStream in=new ByteArrayInputStream(data);
			int len = 0;
			byte[] buf = new byte[1024];
			while((len = in.read(buf, 0, 1024)) != -1){
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 直接获得文件内容（适用于上传图片对应图片的展现）
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getFile.do", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getFile(@PathParam("name")String name) {
		try {
			return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(UPLOAD_ROOT, name).toString()));
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}
	}
	
	/**
	 * 返回文件下载
	 * @param name
	 * @param response
	 */
	@RequestMapping(value = "/getFileDownload.do", method = RequestMethod.GET)
	public void getFileDownload(@PathParam("name")String name,HttpServletRequest request, HttpServletResponse response) {
		
		File file = new File(UPLOAD_ROOT + name);
		
		if(file.exists()){ //判断文件是否存在
			
			// 文件类型判断
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if(mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType); // application/octet-stream application/force-download
			
			// 文件名编码，解决乱码问题
			String encodedFileName = null;
			String userAgentString = request.getHeader("User-Agent");
			String browser = UserAgent.parseUserAgentString(userAgentString).getBrowser().getGroup().getName();
			if(browser.equals("Chrome") || browser.equals("Internet Exploer") || browser.equals("Safari")) {
				try {
					encodedFileName = URLEncoder.encode(name, "utf-8").replaceAll("\\+", "%20");
				} catch (UnsupportedEncodingException e) {
					logger.error("文件名编码异常！", e);
				}
			} else {
				try {
					encodedFileName = MimeUtility.encodeWord(name);
				} catch (UnsupportedEncodingException e) {
					logger.error("文件名编码异常！", e);
				}
			}
			response.setHeader("Content-Disposition", "attachment;fileName=" + encodedFileName);
			
			byte[] buffer = new byte[1024];
			FileInputStream fis = null; //文件输入流
			BufferedInputStream bis = null;
			
			OutputStream os = null; //输出流
			try {
				os = response.getOutputStream();
				fis = new FileInputStream(file); 
				bis = new BufferedInputStream(fis);
				int i = bis.read(buffer);
				while(i != -1){
					os.write(buffer);
					i = bis.read(buffer);
				}
			
			} catch (Exception e) {
				logger.error("文件下载，流操作异常！", e);
			} finally {
				try {
					os.close();
					bis.close();
					fis.close();
				} catch (IOException e) {
					logger.error("文件下载，流关闭异常！", e);
				}
			}
		}
	}
	
	/**
	 * 文件类上传
	 * 
	 * 包括除了头像图片以外的文件上传
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/fileUpload.do")
	@ResponseBody
	public Map<String, Object> fileUpload(@RequestParam("file") MultipartFile file) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", "0");
		resultMap.put("msg", "上传成功！");
		
		if (!file.isEmpty()) {
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			
			// 文件校验
			String fileName = file.getOriginalFilename();
			
			// 判断文件类型
			String contentType = file.getContentType();
			String fileType = FileType.NOT_IMAGE_FILE.getKey();
			if(contentType.indexOf("image") != -1){
				fileType = FileType.IMAGE.getKey();
			}
			
			// 检测文件是否存在
			File localFile = new File(UPLOAD_ROOT + fileName);
			if(localFile.exists()){
				logger.info("待上传的文件已存在！");
				
				if(fileType.equals(FileType.IMAGE.getKey())){ // 图片类型
					dataMap.put("src", "/file/getFile.do?name=" + fileName);
				} else { // 非图片类型
					dataMap.put("src", "/file/getFileDownload.do?name=" + fileName);
					dataMap.put("name", fileName);
				}
				
				resultMap.put("data", dataMap);
				return resultMap;
			}
			
			// 检测文件
			boolean verifyResult = fileVerify(file);
			if(!verifyResult){
				
			}
			
			try {
				// 保存文件至指定目录
				Path path = Paths.get(UPLOAD_ROOT, fileName);
				Files.copy(file.getInputStream(), path);
				
				if(fileType.equals(FileType.IMAGE.getKey())){ // 图片类型
					dataMap.put("src", "/file/getFile.do?name=" + fileName);
				} else { // 非图片类型
					dataMap.put("src", "/file/getFileDownload.do?name=" + fileName);
					dataMap.put("name", fileName);
				}
				
				resultMap.put("data", dataMap);
				
			} catch (IOException | RuntimeException e) {
				logger.error("文件上传异常！", e);
				resultMap.put("code", "1");
				resultMap.put("msg", "上传失败！");
			}
		} else {
			resultMap.put("code", "2");
			resultMap.put("msg", "文件为空！");
		}
	
	return resultMap;
	
	}
	
	/**
	 * 文件格式及大小检验
	 * @param file
	 * @param type 类型（image需要针对图片格式进行校验）
	 * @return
	 */
	public boolean fileVerify(MultipartFile file) {
		
//		// 图片支持类型判断
//		if("image".equals(type)){
//			if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/gif") 
//					&& !file.getContentType().equals("image/png") && !file.getContentType().equals("image/x-icon")) { // 文件类型判断
//				return false;
//			}
//		}
		
		// 文件大小判断（默认5mb，请注意application.properties下对于文件大小的限制）
		if(file.getSize() > 5 * 1024 * 1024) { 
			return false;
		}
		
		return true;
	}
	
	/**
	 * 文件大小超限异常处理
	 * @param request
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler(value = MultipartException.class) // 
	@ResponseBody
	public Map<String, Object> fileErrorHandler(HttpServletRequest request, Exception e) {
		
		logger.error("文件大小超限异常!");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("code", "999");
		resultMap.put("msg", "文件大小超限，单个文件最大为5MB！");
		return resultMap;
	}
	
}
