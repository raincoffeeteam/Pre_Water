package cn.sfw.zju.system.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static Long convertTimeToLong(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
			date = dateFormat.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
/*	public static Long convertBeginTimeToLong(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/DD/YYYY h:mm A");
        String beginDate = time.split("-")[0];
        Date date = null;
        try {
			date = dateFormat.parse(beginDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
	
	public static Long convertEndTimeToLong(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/DD/YYYY h:mm A");
        String endDate = time.split("-")[1];
        Date date = null;
        try {
			date = dateFormat.parse(endDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}*/
	public static Long convertTimeToLong(Date date){
		return date.getTime();
	}
	
	public static String convertLongToTime(Long time){
		Date date= new Date(Long.valueOf(time));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s=dateFormat.format(date);
		return s;
	}
	
	public static Date resetDate(Date date,int day){
		 Calendar calendar=Calendar.getInstance();
	     calendar.setTime(date);
	     calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+day);
	     return calendar.getTime();
	}
	
	public static String month(String pTime){
		return pTime.split("-")[1];
	}
	public static String day(String pTime){
		String date = pTime.split(" ")[0];
		return date.split("-")[2];
	}
	public static String week(String pTime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		int dayForWeek = 0;
		try {
			c.setTime(format.parse(pTime));
			if(c.get(Calendar.DAY_OF_WEEK) == 1){
				dayForWeek = 7;
			}else{
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return String.valueOf(dayForWeek);
	}
	public static String is_holiday(String pTime){
		return "0";
	}
	
	
	public static void main(String args[]){
		Long time= convertTimeToLong("2016-11-20 00:00:00");
		System.out.println(convertLongToTime(1416326400000L));
		//1442678400000
		System.out.println(time-1442678400000L);
		System.out.println(convertLongToTime(1415875800000L));
		System.out.println(resetDate(new Date(),10).toLocaleString());
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");  
		try {
			c.setTime(sdf1.parse(1993+"-"+2+"-"+3));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		System.out.println(sdf1.format(c.getTime()));
		System.out.println(week("2016-11-15"));
	}
	
	
}
