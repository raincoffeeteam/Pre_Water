package cn.sfw.zju.quartz.service;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import cn.sfw.zju.system.dao.Interval_DDao;
import cn.sfw.zju.system.dao.New_Interval_DDao;
import cn.sfw.zju.system.service.Interval_DService;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.DateUtil;
import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.New_Interval_D;

public class QuartzJob implements Job {
	@Autowired 
	private Interval_DDao dayDao;
	@Autowired
	private Interval_DService dayDaoService;
	@Autowired
	private New_Interval_DService newDayService;
	
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
	
	@Transactional
	public void autoImport(Long time) throws IOException{
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		//从日期表中读数据转化成二进制格式插入到新表中
		List<Interval_D> intervalList =dayDaoService.getInterval_DByTime(time);
		//System.out.println(intervalList);
		for(int i=0;i<intervalList.size();i++){
			New_Interval_D newInterval = new New_Interval_D();
			
			newInterval.setCst_id(intervalList.get(i).getCst_id());
			newInterval.setCst_name(intervalList.get(i).getCst_name());
			newInterval.setCdate(intervalList.get(i).getCdate());
			newInterval.setMdi_ts(intervalList.get(i).getMdi_ts());
			newInterval.setR(intervalList.get(i).getR());
			newInterval.setR1(intervalList.get(i).getR1());
			newInterval.setR2(intervalList.get(i).getR2());
			newInterval.setR3(intervalList.get(i).getR3());
			newInterval.setR7(intervalList.get(i).getR7());
			newInterval.setA3(intervalList.get(i).getA3());
			
			int m = Integer.parseInt(intervalList.get(i).getM());
			int d = Integer.parseInt(intervalList.get(i).getD());
			int w = Integer.parseInt(intervalList.get(i).getW());
			int is_Holiday = Integer.parseInt(intervalList.get(i).getIs_holidays());
			
			switch(m){
			case 1:newInterval.setM1(1); break;
			case 2:newInterval.setM2(1); break;
			case 3:newInterval.setM3(1); break;
			case 4:newInterval.setM4(1); break;
			case 5:newInterval.setM5(1); break;
			case 6:newInterval.setM6(1); break;
			case 7:newInterval.setM7(1); break;
			case 8:newInterval.setM8(1); break;
			case 9:newInterval.setM9(1); break;
			case 10:newInterval.setM10(1); break;
			case 11:newInterval.setM11(1); break;
			case 12:newInterval.setM12(1); break;
			}
			
			switch(d){
			case 1:newInterval.setD1(1); break;
			case 2:newInterval.setD2(1); break;
			case 3:newInterval.setD3(1); break;
			case 4:newInterval.setD4(1); break;
			case 5:newInterval.setD5(1); break;
			case 6:newInterval.setD6(1); break;
			case 7:newInterval.setD7(1); break;
			case 8:newInterval.setD8(1); break;
			case 9:newInterval.setD9(1); break;
			case 10:newInterval.setD10(1); break; 
			case 11:newInterval.setD11(1); break;
			case 12:newInterval.setD12(1); break;
			case 13:newInterval.setD13(1); break;
			case 14:newInterval.setD14(1); break;
			case 15:newInterval.setD15(1); break;
			case 16:newInterval.setD16(1); break;
			case 17:newInterval.setD17(1); break;
			case 18:newInterval.setD18(1); break;
			case 19:newInterval.setD19(1); break;
			case 20:newInterval.setD20(1); break;
			case 21:newInterval.setD21(1); break;
			case 22:newInterval.setD22(1); break;
			case 23:newInterval.setD23(1); break;
			case 24:newInterval.setD24(1); break;
			case 25:newInterval.setD25(1); break;
			case 26:newInterval.setD26(1); break;
			case 27:newInterval.setD27(1); break;
			case 28:newInterval.setD28(1); break;
			case 29:newInterval.setD29(1); break;
			case 30:newInterval.setD30(1); break;
			case 31:newInterval.setD31(1); break;
			
			}
			switch(w){
			case 1:newInterval.setW1(1); break;
			case 2:newInterval.setW2(1); break;
			case 3:newInterval.setW3(1); break;
			case 4:newInterval.setW4(1); break;
			case 5:newInterval.setW5(1); break;
			case 6:newInterval.setW6(1); break;
			case 7:newInterval.setW7(1); break;
			}
			
			switch(is_Holiday){
			case 0:newInterval.setNot_holidays(1);;
			case 1:newInterval.setIs_holidays(1);;
			}
			//System.out.println(newInterval);
			newDayService.setNew_Interval_D(newInterval);
		}
	}
}