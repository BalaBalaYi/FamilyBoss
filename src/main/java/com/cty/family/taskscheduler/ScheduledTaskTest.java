package com.cty.family.taskscheduler;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTaskTest {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Scheduled(cron = "0/10 * * ? * *")
	public void reportCurrentTime(){
		System.out.println("定时任务测试：" + sdf.format(new Date()));
	}
}
