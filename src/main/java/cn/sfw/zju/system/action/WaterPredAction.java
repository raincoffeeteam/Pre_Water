package cn.sfw.zju.system.action;

import java.util.ArrayList;
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
import cn.sfw.zju.common.util.DateUtil;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.core.Utils;

@Controller
@RequestMapping("/system/waterPredAction")
/**
 * 
 * @author 29586
 *
 */
public class WaterPredAction {
	private final static Log log = LogFactory.getLog(WaterPredAction.class);
	
	@Autowired
	private New_Interval_DService interval_DService;
	
	private WekaUtils wekaUtils = WekaUtils.getInstance();
	private String cst_id;
	private Long maxTime;
	/**
	 * 
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/predWater",method = RequestMethod.POST)
	public Message predWater(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map){
		Message message = new Message();
		
		this.cst_id =(String) map.get("cst_id");
		
		//查询该测点的数据起始和终止日期，获取train instances
		Map<String, Object> dateMap =interval_DService.getMaxDateAndMinDateByCstId(cst_id); 
		dateMap.put("cst_id", cst_id);
		
		//获取最大时间
		this.maxTime= interval_DService.getMaxTimeByCstId(cst_id);
		//通过id获取所有数据作为训练集
		List<New_Interval_D> list= interval_DService.getAllByCstId(cst_id);
		Instances train=InstancesUtils.createInstancesFromNID(list);
		
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
		
		//long today=0;
		//定义预测结果
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<New_Interval_D> testList = new ArrayList<>();
		//预测一天的数据
		if(forcastDays == 1){
			long today=0;
			long lastWeek = maxTime - 604800000L;		
			double 	lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
			//穿件测试集
			createOnedayTestList(today,lastWeekWaterValue,testList);
			//进行预测
			Instances test= InstancesUtils.createInstancesFromNID(testList);
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
					resultmap.put("cDate", testList.get(i).getCdate());
					resultmap.put("predictValue", predictValue);
					resultList.add(resultmap);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("error");
				}
			}
			
		}else if(forcastDays==7){ //预测7天的数据
			long today = 0;
			//创建测试集
			for(int i=0;i<forcastDays;i++){
				
				long lastWeek = maxTime - 604800000L + i*86400000L;
				double lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);		
				if(i==0){
					createOnedayTestList(today,lastWeekWaterValue,testList);
				}
				else{
					createBeyoundSevenTestList(i,today,lastWeekWaterValue,testList);
				}		 
			}
			//进行预测
			Instances test= InstancesUtils.createInstancesFromNID(testList);
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
					resultmap.put("cDate", testList.get(n).getCdate());
					resultmap.put("predictValue", predictValue);
					resultList.add(resultmap);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("error");
				}
			}
			
		}else if(forcastDays==15){ //预测15天的数据
			
			//Instances test;
			long today=0;
				//进行预测
				for(int i=0;i<forcastDays;i++){
					long lastWeek = maxTime - 604800000L + i*86400000L;
					double lastWeekWaterValue;	
					lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
					if(i==0){
						createOnedayTestList(today,lastWeekWaterValue,testList);
						
					}else if(0<i&&i<7){
						
						createBeyoundSevenTestList(i,today,lastWeekWaterValue,testList);
						
					}else{
						lastWeekWaterValue =Double.parseDouble(resultList.get(i-6).get("predictValue").toString());
						createBeyoundSevenTestList(i,today,lastWeekWaterValue,testList);
					}
					//testList.add(interval_D);
			}
			
				//进行预测
				Instances test= InstancesUtils.createInstancesFromNID(testList);
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
						resultmap.put("cDate", testList.get(n).getCdate());
						resultmap.put("predictValue", predictValue);
						resultList.add(resultmap);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("error");
					}
				}	
				
		}
		
		message.setData(resultList);
		message.setCode("SUCCESS");
		return message;
	}
	//预测一天的数据
	private void createOnedayTestList(long today,double lastWeekWaterValue,List<New_Interval_D> testList){
		Map<String, Object> parmMap =new HashMap<>();
		parmMap.put("cst_id", cst_id);
		parmMap.put("mdi_ts", maxTime);
		New_Interval_D beforeDay = interval_DService.getNew_Interval_DByCstIdAndTs(parmMap);	
		New_Interval_D interval_D = new New_Interval_D();
		
		today = beforeDay.getMdi_ts() + 86400000L;//获取今天的时间，毫秒表示
		//将毫秒表示转换成日期格式
		String date = DateUtil.convertLongToTime(today);
		int m = Integer.parseInt(DateUtil.month(date));
		int d = Integer.parseInt(DateUtil.day(date));
		int w = Integer.parseInt(DateUtil.week(date));
		interval_D.setCdate(date);
		getNew_Interval_D(m,d,w,interval_D);
		interval_D.setR1(beforeDay.getR());
		interval_D.setR2(beforeDay.getR1());
		interval_D.setR3(beforeDay.getR2());
		interval_D.setR7(lastWeekWaterValue);
		interval_D.setA3((beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
		interval_D.setR(0.0);
		testList.add(interval_D);
		
	} 
	
	private void createBeyoundSevenTestList(int i,long today,double lastWeekWaterValue,List<New_Interval_D> testList){
		today = today + 86400000L;
		double r = Double.parseDouble(testList.get(i-1).getR1().toString());
		double r1 = Double.parseDouble(testList.get(i-1).getR2().toString());
		double r2 = Double.parseDouble(testList.get(i-1).getR3().toString());
		String date = null;
		date = DateUtil.convertLongToTime(today);
		New_Interval_D interval_D = new New_Interval_D();
		
		int m = Integer.parseInt(DateUtil.month(date));
		int d = Integer.parseInt(DateUtil.day(date));
		int w = Integer.parseInt(DateUtil.week(date));
		interval_D.setCdate(date);
		getNew_Interval_D(m,d,w,interval_D);
		interval_D.setR1(r);
		interval_D.setR2(r1);
		interval_D.setR3(r2);
		interval_D.setR7(lastWeekWaterValue);
		interval_D.setA3((r+r1+r2)/3);
		interval_D.setR(0.0); 
		
		testList.add(interval_D);
	}
	private void getNew_Interval_D(int m,int d,int w,New_Interval_D interval_D){
		switch(m){
		case 1:interval_D.setM1(1); break;
		case 2:interval_D.setM2(1); break;
		case 3:interval_D.setM3(1); break;
		case 4:interval_D.setM4(1); break;
		case 5:interval_D.setM5(1); break;
		case 6:interval_D.setM6(1); break;
		case 7:interval_D.setM7(1); break;
		case 8:interval_D.setM8(1); break;
		case 9:interval_D.setM9(1); break;
		case 10:interval_D.setM10(1); break;
		case 11:interval_D.setM11(1); break;
		case 12:interval_D.setM12(1); break;
		}
		
		switch(d){
		case 1:interval_D.setD1(1); break;
		case 2:interval_D.setD2(1); break;
		case 3:interval_D.setD3(1); break;
		case 4:interval_D.setD4(1); break;
		case 5:interval_D.setD5(1); break;
		case 6:interval_D.setD6(1); break;
		case 7:interval_D.setD7(1); break;
		case 8:interval_D.setD8(1); break;
		case 9:interval_D.setD9(1); break;
		case 10:interval_D.setD10(1); break; 
		case 11:interval_D.setD11(1); break;
		case 12:interval_D.setD12(1); break;
		case 13:interval_D.setD13(1); break;
		case 14:interval_D.setD14(1); break;
		case 15:interval_D.setD15(1); break;
		case 16:interval_D.setD16(1); break;
		case 17:interval_D.setD17(1); break;
		case 18:interval_D.setD18(1); break;
		case 19:interval_D.setD19(1); break;
		case 20:interval_D.setD20(1); break;
		case 21:interval_D.setD21(1); break;
		case 22:interval_D.setD22(1); break;
		case 23:interval_D.setD23(1); break;
		case 24:interval_D.setD24(1); break;
		case 25:interval_D.setD25(1); break;
		case 26:interval_D.setD26(1); break;
		case 27:interval_D.setD27(1); break;
		case 28:interval_D.setD28(1); break;
		case 29:interval_D.setD29(1); break;
		case 30:interval_D.setD30(1); break;
		case 31:interval_D.setD31(1); break;
		
		}
		switch(w){
		case 1:interval_D.setW1(1); break;
		case 2:interval_D.setW2(1); break;
		case 3:interval_D.setW3(1); break;
		case 4:interval_D.setW4(1); break;
		case 5:interval_D.setW5(1); break;
		case 6:interval_D.setW6(1); break;
		case 7:interval_D.setW7(1); break;
		}
	}
}
