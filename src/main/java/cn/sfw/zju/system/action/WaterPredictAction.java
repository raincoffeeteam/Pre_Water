package cn.sfw.zju.system.action;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import cn.sfw.zju.common.Message;
import cn.sfw.zju.common.ResponseCode;
import cn.sfw.zju.quartz.service.QuartzJob;
import cn.sfw.zju.system.service.Interval_DService;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.DateUtil;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;


@Controller
@RequestMapping("/system/waterPredictAction")
public class WaterPredictAction {
	
	private final static Log log = LogFactory.getLog(WaterPredictAction.class);
	@Autowired
	private Interval_DService interval_DService;
	
	private WekaUtils wekaUtils=WekaUtils.getInstance();
	
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param map11
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/predictWater/", method = RequestMethod.POST)
	public Message predictWater(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map) throws IOException {
		Message message = new Message();
		System.out.println("aaaaa");
		String cst_id =(String) map.get("cst_id");
		
		//查询该测点的数据起始和终止日期，获取train instances
		Map<String, Object> dateMap =interval_DService.getMaxDateAndMinDateByCstId(cst_id); 
		dateMap.put("cst_id", cst_id);
		
		//获取最大时间
		long maxTime = interval_DService.getMaxTimeByCstId(cst_id);
		//通过id获取所有数据作为训练集
		List<Map<String, Object>> list= interval_DService.getAllByCstId(cst_id);
		Instances train=InstancesUtils.createPreDayInstances(list);
		
		//获取时间间隔timeInterval(假设暂时指定天)
		//int timeInterval=(int) map.get("timeInterval");
		//处理数据	
		try {
			train = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(train))));
		} catch (Exception e1) {
			log.error("instances 数据处理异常");
			e1.printStackTrace();
		}
		//模型训练
	//	String s =(String) map.get("algorithm");
		String s =(String) map.get("algorithm");
		
		Classifier classifier=null;
		CVParameterSelection cvParameterSelection = new CVParameterSelection(); //寻找最优参数得类
		try {
			if(s.equals("tree")){
				String[] options ={"-M", "16", "-N"};
				classifier=wekaUtils.getM5PClassifer(train, options);
				cvParameterSelection.setClassifier(classifier);
				//cvParameterSelection.addCVParameter("M 1 20 20");
				cvParameterSelection.setNumFolds(10);
				cvParameterSelection.buildClassifier(train);
				log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
				classifier= wekaUtils.getM5PClassifer(train, cvParameterSelection.getBestClassifierOptions());
			}else if (s.equals("smoreg")){				
				String[] options={"-C","0.5","-N","0"};
				classifier= wekaUtils.getSMOregClassifer(train, options);
				cvParameterSelection.setClassifier(classifier);
				//cvParameterSelection.addCVParameter("C 0.1 2 20");
				cvParameterSelection.setNumFolds(5);
				cvParameterSelection.buildClassifier(train);
				log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
				classifier=wekaUtils.getSMOregClassifer(train, cvParameterSelection.getBestClassifierOptions());
			}else if(s.equals("bp")){
				cvParameterSelection= new CVParameterSelection();
			    String[] options={};
			    classifier= wekaUtils.getBPClassifer(train, options);
			    cvParameterSelection.setClassifier(classifier);
				cvParameterSelection.setNumFolds(10);
				cvParameterSelection.buildClassifier(train);
				System.out.println(Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));				
				classifier= wekaUtils.getBPClassifer(train, cvParameterSelection.getBestClassifierOptions());
			}else{
				log.info("没有该算法");
			}
		} catch (Exception e) {
			log.error("训练模型失败");
		}
		
		//获取时间间隔forcastDays
		int forcastDays=Integer.parseInt(map.get("forcastDays").toString());
		
		long today=0;
		//定义预测结果
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<Map<String, Object>> testList = new ArrayList<>();
		//预测一天的数据
		if(forcastDays == 1){
			Map<String, Object> parmMap =new HashMap<>();
			parmMap.put("cst_id", cst_id);
			//@undo 
			//parmMap.put("mdi_ts", 1474387200000L);
			long lastWeek = maxTime - 604800000L;
			//long today;
			String date = null;
			//通过时间获取七天前的用数量
			double 	lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
			parmMap.put("mdi_ts", maxTime);
			Interval_D beforeDay = interval_DService.getInterval_DByCstIdAndTs(parmMap);		 	
			Map<String,Object> interval_D = new HashMap<>();
			today = beforeDay.getMdi_ts() + 86400000L;//获取今天的时间，毫秒表示
			System.out.println(today);
			//将毫秒表示转换成日期格式
			date = DateUtil.convertLongToTime(today);
			interval_D.put("Date",date);
			interval_D.put("M", DateUtil.month(date));
			interval_D.put("D",DateUtil.day(date));
			interval_D.put("W",DateUtil.week(date));
			interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
			interval_D.put("R1",beforeDay.getR());
			interval_D.put("R2",beforeDay.getR1());
			interval_D.put("R3",beforeDay.getR2());
			interval_D.put("R7",lastWeekWaterValue);
			interval_D.put("A3",(beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
			interval_D.put("R",0.0);
			
			testList.add(interval_D);
			

			Instances test= InstancesUtils.createPreDayInstances(testList);
			//二进制化
			try {
				test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
			} catch (Exception e1) {
				log.error("instances 数据处理异常");
				e1.printStackTrace();
			}
			double sum=test.numInstances();
			System.out.println(sum);
			for (int i = 0;i < sum; i++){
				try {
					double predictValue=classifier.classifyInstance(test.instance(i));
					Map<String, Object> resultmap= new HashMap<>();
					resultmap.put("cst_id",cst_id);
					resultmap.put("cDate", testList.get(i).get("Date"));
					resultmap.put("predictValue", predictValue);
					resultList.add(resultmap);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("error");
				}
			}
			
			/*interval_D.setMdi_ts(today);
			interval_D.setCdate(date);
			interval_D.setM(DateUtil.month(date));
			interval_D.setD(DateUtil.day(date));
			interval_D.setW(DateUtil.week(date));
			interval_D.setIs_holidays(DateUtil.is_holiday(date));
			interval_D.setR1(beforeDay.getR());
			interval_D.setR2(beforeDay.getR1());
			interval_D.setR3(beforeDay.getR2());
			interval_D.setR7(lastWeekWaterValue);
			interval_D.setA3((beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
			interval_D.setR(0.0);*/
			
		}else if(forcastDays==7){ //预测7天的数据
			//进行预测
			for(int i=0;i<forcastDays;i++){
				Map<String,Object> interval_D = new HashMap<>();
				Map<String, Object> parmMap =new HashMap<>();
				String date = null;
				parmMap.put("cst_id", cst_id);

				long lastWeek = maxTime - 604800000L + i*86400000L;
			//	long today;
				double lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
				Interval_D beforeDay =new Interval_D(); //保存预测的第一天的前一天数据
				parmMap.put("mdi_ts", maxTime);
				if(i==0){
					beforeDay = interval_DService.getInterval_DByCstIdAndTs(parmMap);
					today = beforeDay.getMdi_ts() + 86400000L;//获取今天的时间，毫秒表示
					System.out.println(today);
					//将毫秒表示转换成日期格式
					date = DateUtil.convertLongToTime(today);
					interval_D.put("Date",date);
					interval_D.put("M", DateUtil.month(date));
					interval_D.put("D",DateUtil.day(date));
					interval_D.put("W",DateUtil.week(date));
					interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
					interval_D.put("R1",beforeDay.getR());
					interval_D.put("R2",beforeDay.getR1());
					interval_D.put("R3",beforeDay.getR2());
					interval_D.put("R7",lastWeekWaterValue);
					interval_D.put("A3",(beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
					interval_D.put("R",0.0);
				}
				else{
					today = today + 86400000L;
					double r = Double.parseDouble(testList.get(i-1).get("R1").toString());
					double r1 = Double.parseDouble(testList.get(i-1).get("R2").toString());
					double r2 = Double.parseDouble(testList.get(i-1).get("R3").toString());
					System.out.println(today);
					date = DateUtil.convertLongToTime(today);
					interval_D.put("Date",date);
					interval_D.put("M",DateUtil.month(date));
					interval_D.put("D",DateUtil.day(date));
					interval_D.put("W",DateUtil.week(date));
					interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
					interval_D.put("R1",r);
					interval_D.put("R2",r1);
					interval_D.put("R3",r2);
					interval_D.put("R7",lastWeekWaterValue);
					interval_D.put("A3",(r+r1+r2)/3);
					interval_D.put("R",0.0); 
				}		 
				testList.add(interval_D);
			}
			
			Instances test= InstancesUtils.createPreDayInstances(testList);
			//二进制化
			try {
				test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
			} catch (Exception e1) {
				log.error("instances 数据处理异常");
				e1.printStackTrace();
			}
			double sum=test.numInstances();
			System.out.println(sum);
			for (int n = 0;n < sum; n++){
				try {
					double predictValue=classifier.classifyInstance(test.instance(n));
					Map<String, Object> resultmap= new HashMap<>();
					resultmap.put("cst_id",cst_id);
					resultmap.put("cDate", testList.get(n).get("Date"));
					resultmap.put("predictValue", predictValue);
					resultList.add(resultmap);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("error");
				}
			}
			
		}else if(forcastDays==15){ //预测15天的数据
			
			Instances test;
				//进行预测
				for(int i=0;i<forcastDays;i++){
					Map<String,Object> interval_D = new HashMap<>();
					Map<String, Object> parmMap =new HashMap<>();
					String date = null;
					parmMap.put("cst_id", cst_id);
	
					long lastWeek = maxTime - 604800000L + i*86400000L;
				//	long today;
					double lastWeekWaterValue;
					Interval_D beforeDay =new Interval_D(); //保存预测的第一天的前一天数据
					parmMap.put("mdi_ts", maxTime);
					if(i==0){
						lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
						beforeDay = interval_DService.getInterval_DByCstIdAndTs(parmMap);
						today = beforeDay.getMdi_ts() + 86400000L;//获取今天的时间，毫秒表示
						System.out.println(today);
						//将毫秒表示转换成日期格式
						date = DateUtil.convertLongToTime(today);
						interval_D.put("Date",date);
						interval_D.put("M", DateUtil.month(date));
						interval_D.put("D",DateUtil.day(date));
						interval_D.put("W",DateUtil.week(date));
						interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
						interval_D.put("R1",beforeDay.getR());
						interval_D.put("R2",beforeDay.getR1());
						interval_D.put("R3",beforeDay.getR2());
						interval_D.put("R7",lastWeekWaterValue);
						interval_D.put("A3",(beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
						interval_D.put("R",0.0);
						
						testList.add(interval_D);
						test= InstancesUtils.createPreDayInstances(testList);
						//二进制化
						try{
								test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
								log.error("instances 数据处理异常");
								e1.printStackTrace();
						}
						try{
							Map<String, Object> resultmap= new HashMap<>();
							double predictValue=classifier.classifyInstance(test.instance(i));
							resultmap.put("cst_id",cst_id);
							resultmap.put("cDate", testList.get(i).get("Date"));
							resultmap.put("predictValue", predictValue);
							resultList.add(resultmap);
						} catch (Exception e){
							e.printStackTrace();
							log.error("error");
						}
						
					}else if(0<i&&i<7){
						lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
						today = today + 86400000L;
						double r = Double.parseDouble(testList.get(i-1).get("R1").toString());
						double r1 = Double.parseDouble(testList.get(i-1).get("R2").toString());
						double r2 = Double.parseDouble(testList.get(i-1).get("R3").toString());
						System.out.println(today);
						date = DateUtil.convertLongToTime(today);
						interval_D.put("Date",date);
						interval_D.put("M",DateUtil.month(date));
						interval_D.put("D",DateUtil.day(date));
						interval_D.put("W",DateUtil.week(date));
						interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
						interval_D.put("R1",r);
						interval_D.put("R2",r1);
						interval_D.put("R3",r2);
						interval_D.put("R7",lastWeekWaterValue);
						interval_D.put("A3",(r+r1+r2)/3);
						interval_D.put("R",0.0); 
						
						testList.add(interval_D);
						test= InstancesUtils.createPreDayInstances(testList);
						//二进制化
						try{
								test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
								log.error("instances 数据处理异常");
								e1.printStackTrace();
						}
						try{
							Map<String, Object> resultmap= new HashMap<>();
							double predictValue=classifier.classifyInstance(test.instance(i));
							System.out.println(predictValue);
							resultmap.put("cst_id",cst_id);
							resultmap.put("cDate", testList.get(i).get("Date"));
							resultmap.put("predictValue", predictValue);
							resultList.add(resultmap);
						} catch (Exception e){
							e.printStackTrace();
							log.error("error");
						}
						
					}else{
						lastWeekWaterValue =Double.parseDouble(resultList.get(i-6).get("predictValue").toString());
						today = today + 86400000L;
						double r = Double.parseDouble(testList.get(i-1).get("R1").toString());
						double r1 = Double.parseDouble(testList.get(i-1).get("R2").toString());
						double r2 = Double.parseDouble(testList.get(i-1).get("R3").toString());
						System.out.println(today);
						date = DateUtil.convertLongToTime(today);
						interval_D.put("Date",date);
						interval_D.put("M",DateUtil.month(date));
						interval_D.put("D",DateUtil.day(date));
						interval_D.put("W",DateUtil.week(date));
						interval_D.put("IS_HOLIDAYS",DateUtil.is_holiday(date));
						interval_D.put("R1",r);
						interval_D.put("R2",r1);
						interval_D.put("R3",r2);
						interval_D.put("R7",lastWeekWaterValue);
						interval_D.put("A3",(r+r1+r2)/3);
						interval_D.put("R",0.0); 
						
						testList.add(interval_D);
						test= InstancesUtils.createPreDayInstances(testList);
						//二进制化
						try{
								test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
								log.error("instances 数据处理异常");
								e1.printStackTrace();
						}
						try{
							Map<String, Object> resultmap= new HashMap<>();
							double predictValue=classifier.classifyInstance(test.instance(i));
							System.out.println(predictValue);
							resultmap.put("cst_id",cst_id);
							resultmap.put("cDate", testList.get(i).get("Date"));
							resultmap.put("predictValue", predictValue);
							resultList.add(resultmap);
						} catch (Exception e){
							e.printStackTrace();
							log.error("error");
						}
						
					}
					//testList.add(interval_D);
			}
			
			//double sum=test.numInstances();
			//System.out.println(sum);
				
		}
		
		message.setData(resultList);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
}
