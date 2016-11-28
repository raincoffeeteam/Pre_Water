package cn.sfw.zju.quartz.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cn.sfw.zju.common.util.DateUtil;
import cn.sfw.zju.system.dao.Interval_DDao;

public class QuartzJob implements Job {
	@Autowired 
	private Interval_DDao dayDao;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			autoSave();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+ "★★★★★★★★★★★");  
	}
	
	@Transactional
	public void autoSave() throws IOException{
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		Date date= DateUtil.resetDate(new Date(), -1);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String time=dateFormat.format(date)+" 00:00:00";
		System.out.println(time);
        Long t=DateUtil.convertTimeToLong(time);
		dayDao.autoSave(t);
		System.out.println("success");
	}
	
}