package com.cty.family.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 * 
 * @ComponentScan：扫描并配置"com.cty.family.taskscheduler"下的定时任务
 * @EnableScheduling：启动定时任务
 * 
 * @author 陈天熠
 *
 */
@Configuration
@ComponentScan("com.cty.family")
@EnableScheduling
public class TaskSchedulerConfig {

}
