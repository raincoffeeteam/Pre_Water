package cn.sfw.zju.quartz.service;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;
public class QuartzSchedulerListener implements ServletContextListener{ 
	public void contextDestroyed(ServletContextEvent arg0) { 
		//銷毀任務
		//調用QuartzManager裡面的方法（一種辦法）
		QuartzManager.shutdownJobs();
	}
	
	public void contextInitialized(ServletContextEvent arg0) { 
		//初始化任務
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		QuartzManager.shutdownJobs();
		//每晚1点执行下插入数据库操作。
		String time="0 0 1 * * ?";
		//String t="0 0/1 * * * ?";
		System.out.println("___________________________________________________");
		QuartzManager.addJob("cont", QuartzJob.class, time);
	 }
}