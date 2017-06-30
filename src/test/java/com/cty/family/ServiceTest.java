package com.cty.family;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cty.family.entity.ImageEntity;
import com.cty.family.entity.UserEntity;
import com.cty.family.service.ImageService;
import com.cty.family.service.SystemService;
import com.cty.family.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FamilyBossApplication.class)
public class ServiceTest {

	@Autowired
	private UserService userService;
	@Autowired
	private ImageService imageService;
	@Autowired
	private SystemService systemService;
	
	@Test
	@Ignore
	public void testUserService(){
		UserEntity user = userService.getUserInfoById(1);
		System.out.println(user.toString());
	}
	
	@Test
	@Ignore
	public void testImageService() throws Exception {
		
		// 添加图像
//		BufferedInputStream in = new BufferedInputStream(new FileInputStream("D:\\temp\\img\\1.jpg"));
//		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
//		
//		byte[] temp = new byte[1024];
//		int size = 0;
//		while ((size = in.read(temp)) != -1) {
//			out.write(temp, 0, size);
//		}
//		in.close();
//		byte[] content = out.toByteArray();
//		
//		ImageEntity image = new ImageEntity();
//		image.setName("test1");
//		image.setDesc("test1_desc");
//		image.setContent(content);
//		
//		imageService.saveImage(image);
		
		// 查询图像
		ImageEntity image = imageService.getImageByName("test1");
		System.out.println(image.toString());
		
	}
	
	@Test
//	@Ignore
	public void testSystemService(){
		System.out.println(systemService.getOsInfo());
	}
}
