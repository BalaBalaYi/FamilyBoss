package com.cty.family;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cty.family.service.IMService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = FamilyBossApplication.class)
public class IMTest {

	@Autowired
	private IMService imService;
	
	@Test
	public void initTest(){
		System.out.println(imService.init(1));
	}
	
}
