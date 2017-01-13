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
import cn.sfw.zju.common.ResponseCode;
import cn.sfw.zju.system.service.Interval_HService;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.DateUtil;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.Interval_H;
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
	@Autowired
	private Interval_HService interval_HService;
	
	private WekaUtils wekaUtils = WekaUtils.getInstance();
	private String cst_id;
	private long maxTime;
	private long today=0;
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
		//返回类型
		Message message = new Message();
		
		
		//定义训练用的Instances
		Instances train=null;
		//定义分类器
		Classifier classifier=null;
		//定义预测结果
		List<Map<String, Object>> resultList = new ArrayList<>();
		List<New_Interval_D> testList = new ArrayList<>();
		List<Interval_H> h_testList = new ArrayList<>();
		//获取时间间隔timeInterval(假设暂时指定天)
		String timeInterval=map.get("timeInterval").toString();
		System.out.println(timeInterval);
		//获取时间间隔forcastDays
		int forcastDays=Integer.parseInt(map.get("forcastDays").toString());
		//获取选择算法
		String s =(String) map.get("algorithm");
		
		this.cst_id =(String) map.get("cst_id");
		
		//按照日创建训练集
		if(timeInterval.equals("day")){
			//从数据库获取训练集
			List<New_Interval_D> list = new ArrayList<>();
			//获取最大时间
			this.maxTime= interval_DService.getMaxTimeByCstId(cst_id);
			//通过id获取所有数据作为训练集
			list= interval_DService.getAllByCstId(cst_id);
			train=InstancesUtils.createInstancesFromNID(list);
		}else if(timeInterval.equals("hour")){      //按照时创建训练集
			//从数据库获取训练集
			List<Interval_H> list = new ArrayList<>();
			//获取最大时间
			this.maxTime = interval_HService.getMaxTimeByCstId(cst_id);
			list = interval_HService.getAllByCstId(cst_id);
			train=InstancesUtils.createInstancesFromIH(list);
		}	
			
		//训练模型	
			try {
				train = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(train))));
			} catch (Exception e1) {
				log.error("instances 数据处理异常");
				e1.printStackTrace();
			}			
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
			
			//预测一天的数据
			if(forcastDays == 1){
				Instances test=null;
				//按照不同的训练集模型进行预测
				if(timeInterval.equals("day")){
					
					long lastWeek = maxTime - 604800000L;		
					double 	lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
					//创建测试集
					createOnedayTestList(lastWeekWaterValue,testList);
					//创建测试集
					test= InstancesUtils.createInstancesFromNID(testList);
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
							if(timeInterval.equals("day")){
								resultmap.put("cDate", testList.get(i).getCdate());
								testList.get(i).setR(predictValue);
							}else if(timeInterval.equals("hour")){
								resultmap.put("cDate", h_testList.get(i).getCdate());
								h_testList.get(i).setH0(predictValue);
							}
							resultmap.put("predictValue", predictValue);
							resultList.add(resultmap);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("error");
						}
					}
					today = 0;
				}else if(timeInterval.equals("hour")){      //按照时创建训练集
					//24小时
					for(int i=0;i<24;i++){
						create24HourTestList(i,h_testList);
						test=InstancesUtils.createInstancesFromIH(h_testList);
						//二进制化
						try {
							test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
							log.error("instances 数据处理异常");
							e1.printStackTrace();
						}
						double sum=test.numInstances();
						System.out.println(sum);
							try {
								double predictValue=classifier.classifyInstance(test.instance(i));
								Map<String, Object> resultmap= new HashMap<>();
								resultmap.put("cst_id",cst_id);
								resultmap.put("cDate", h_testList.get(i).getCdate());
								h_testList.get(i).setH0(predictValue);//每次将预测结果复制给当前时间水量
								System.out.println("预测值："+predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e) {
								e.printStackTrace();
								log.error("error");
							}
						
					}	
					
				}	
				
			}else if(forcastDays==7){ //预测7天的数据
				Instances test = null;
				
				//按照不同的训练集模型进行预测
				if(timeInterval.equals("day")){
					//创建测试集
					for(int i=0;i<forcastDays;i++){
						
						long lastWeek = maxTime - 604800000L + i*86400000L;
						double lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);		
						if(i==0){				
							createOnedayTestList(lastWeekWaterValue,testList);						
						}
						else{
							createBeyoundSevenTestList(i,lastWeekWaterValue,testList);							
						}		 
					}
					//进行预测
					test= InstancesUtils.createInstancesFromNID(testList);
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
							testList.get(n).setR(predictValue);
							resultmap.put("predictValue", predictValue);
							resultList.add(resultmap);
						} catch (Exception e) {
							e.printStackTrace();
							log.error("error");
						}
					}
					today = 0;
				}else if(timeInterval.equals("hour")){      //按照时创建训练集
					int i=0;
					//先预测24小时的
					for(i=0;i<24;i++){
						create24HourTestList(i,h_testList);
						test=InstancesUtils.createInstancesFromIH(h_testList);
						//二进制化
						try {
							test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
							log.error("instances 数据处理异常");
							e1.printStackTrace();
						}
						double sum=test.numInstances();
						System.out.println(sum);
							try {
								double predictValue=classifier.classifyInstance(test.instance(i));
								Map<String, Object> resultmap= new HashMap<>();
								resultmap.put("cst_id",cst_id);
								resultmap.put("cDate", h_testList.get(i).getCdate());
								h_testList.get(i).setH0(predictValue);//每次将预测结果复制给当前时间水量
								System.out.println("预测值："+predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e) {
								e.printStackTrace();
								log.error("error");
							}
						
					}		
					//预测24-48小时的
					for(i=24;i<48;i++){
						create24_48HourTestList(i,h_testList);
						test=InstancesUtils.createInstancesFromIH(h_testList);
						//二进制化
						try {
							test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
							log.error("instances 数据处理异常");
							e1.printStackTrace();
						}
						double sum=test.numInstances();
						System.out.println(sum);
							try {
								double predictValue=classifier.classifyInstance(test.instance(i));
								Map<String, Object> resultmap= new HashMap<>();
								resultmap.put("cst_id",cst_id);
								resultmap.put("cDate", h_testList.get(i).getCdate());
								h_testList.get(i).setH0(predictValue);//每次将预测结果复制给当前时间水量
								System.out.println("预测值："+predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e) {
								e.printStackTrace();
								log.error("error");
							}
						
					}	
					
					//预测48小时之后的
					for(i=48;i<forcastDays*24;i++){
						createAfter48HourTestList(i,h_testList);
						test=InstancesUtils.createInstancesFromIH(h_testList);
						//二进制化
						try {
							test = wekaUtils.toBinary(wekaUtils.normalizeV(wekaUtils.standardizeV(wekaUtils.replaceMissingV(test))));
						} catch (Exception e1) {
							log.error("instances 数据处理异常");
							e1.printStackTrace();
						}
						double sum=test.numInstances();
						System.out.println(sum);
							try {
								double predictValue=classifier.classifyInstance(test.instance(i));
								Map<String, Object> resultmap= new HashMap<>();
								resultmap.put("cst_id",cst_id);
								resultmap.put("cDate", h_testList.get(i).getCdate());
								h_testList.get(i).setH0(predictValue);//每次将预测结果复制给当前时间水量
								System.out.println("预测值："+predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e) {
								e.printStackTrace();
								log.error("error");
							}
					}
					today = 0;
				}	
				
			}else if(forcastDays==15){ //预测15天的数据	
				Instances test = null;
				
				//按照不同的训练集模型进行预测
				if(timeInterval.equals("day")){
					//long today=0;
					//创建测试集
					for(int i=0;i<forcastDays;i++){
						double lastWeekWaterValue;	
						if(i==0){
							long lastWeek = maxTime - 604800000L + i*86400000L;
							System.out.println(lastWeek);
							lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
							createOnedayTestList(lastWeekWaterValue,testList);
							//创建instances
							test= InstancesUtils.createInstancesFromNID(testList);
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
								resultmap.put("cDate", testList.get(i).getCdate());
								testList.get(i).setR(predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e){
								e.printStackTrace();
								log.error("error");
							}
							
						}else if(0<i&&i<7){
							long lastWeek = maxTime - 604800000L + i*86400000L;
							System.out.println(lastWeek);
							lastWeekWaterValue = interval_DService.getRByTime(lastWeek,cst_id);
							createBeyoundSevenTestList(i,lastWeekWaterValue,testList);
							//创建instances
							test= InstancesUtils.createInstancesFromNID(testList);
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
								resultmap.put("cDate", testList.get(i).getCdate());
								testList.get(i).setR(predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e){
								e.printStackTrace();
								log.error("error");
							}
							
							
						}else{
							lastWeekWaterValue =Double.parseDouble(resultList.get(i-6).get("predictValue").toString());
							createBeyoundSevenTestList(i,lastWeekWaterValue,testList);
							//创建instances
							test= InstancesUtils.createInstancesFromNID(testList);
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
								resultmap.put("cDate", testList.get(i).getCdate());
								testList.get(i).setR(predictValue);
								resultmap.put("predictValue", predictValue);
								resultList.add(resultmap);
							} catch (Exception e){
								e.printStackTrace();
								log.error("error");
							}
							
						}
					}
					
					today = 0;	
				}else if(timeInterval.equals("hour")){      //按照时创建训练集
					
					today = 0;
				}				
			}	
		message.setData(resultList);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
	private void create24HourTestList(int i,List<Interval_H> h_testList){
		//预测一小时
		long now = maxTime + i*3600000L;//当前时间，精确到小时
		long yesterDayNow = maxTime-86400000L+i*3600000L;
		long befoeYesterDayNow = maxTime-2*86400000L+i*3600000L; 
		long lastWeekNow = maxTime-7*86400000L+i*3600000L; 
		
		System.out.println(now);
		Interval_H yesterDayNow_interval_H = new Interval_H();//前一天
		Interval_H beforeYesterDayNow_interval_H = new Interval_H();//前两天
		Interval_H lastWeekNow_interval_H = new Interval_H();//上一周
		
		Map<String,Object> yesterDayNowParmMap = new HashMap<>();
		yesterDayNowParmMap.put("cst_id",cst_id);
		yesterDayNowParmMap.put("time", yesterDayNow);
		yesterDayNow_interval_H = interval_HService.getAllByCstIdAndTime(yesterDayNowParmMap);
				
		Map<String,Object> befoeYesterDayNowParmMap = new HashMap<>();
		befoeYesterDayNowParmMap.put("cst_id",cst_id);
		befoeYesterDayNowParmMap.put("time", befoeYesterDayNow);
		beforeYesterDayNow_interval_H = interval_HService.getAllByCstIdAndTime(befoeYesterDayNowParmMap);
				
		Map<String,Object> lastWeekNowParmMap = new HashMap<>();
		lastWeekNowParmMap.put("cst_id",cst_id);
		lastWeekNowParmMap.put("time", lastWeekNow);
		lastWeekNow_interval_H = interval_HService.getAllByCstIdAndTime(lastWeekNowParmMap);
				
		Interval_H interval_H = new Interval_H();
		interval_H.setCst_id(Integer.parseInt(cst_id));
		interval_H.setMdi_ts(now);
		interval_H.setCdate(DateUtil.convertLongToTime(now));
		interval_H.setH0(0.0);
				
		if(i>0){
				System.out.println(h_testList.get(i-1).getH0());
			}	
		if(i==0){
			//获取前一个小时的用水量
			double h1 = interval_HService.getH0ByTime(now-3600000L,cst_id);
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}else{
			double h1 = h_testList.get(i-1).getH0(); 
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}
		if(i<2){
			//获取前两个小时的用水量
			double h2 = interval_HService.getH0ByTime(now-2*3600000L,cst_id);
			System.out.println("前两个小时"+h2);
			interval_H.setH2(h2);
		}else{
			double h2 = h_testList.get(i-2).getH0();
			System.out.println("前两个小时"+h2);
		    interval_H.setH2(h2);
		}	
		//获取前一天这一小时的用水量
		interval_H.setH10(yesterDayNow_interval_H.getH0());
		//获取前一天这一小时后一小时的用水量
		double h11 = interval_HService.getH0ByTime(yesterDayNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH11(h11);
		//获取前一天这一小时前一小时的用水量
		double h12 = interval_HService.getH0ByTime(yesterDayNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH12(h12);
		//获取前两天这一小时的用水量
		interval_H.setH20(beforeYesterDayNow_interval_H.getH0());
		//获取前两天这一小时后一小时的用水量
		double h21 = interval_HService.getH0ByTime(beforeYesterDayNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH21(h21);
		//获取前两天这一小时前一小时的用水量
		double h22 = interval_HService.getH0ByTime(beforeYesterDayNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH22(h22);
		//获取前一周这一小时的用水量
		interval_H.setH70(lastWeekNow_interval_H.getH0());
		//获取前一周这一小时后一小时的用水量
		double h71 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH71(h71);
		//获取前一周这一小时前一小时的用水量
		double h72 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH72(h72);
		
		h_testList.add(interval_H);		
	}
	
	private void create24_48HourTestList(int i,List<Interval_H> h_testList){
		//预测一小时
		long now = maxTime + i*3600000L;//当前时间，精确到小时
		//long yesterDayNow =h_testList.get(i-24).getMdi_ts(); 
		long befoeYesterDayNow = maxTime-2*86400000L+i*3600000L; 
		long lastWeekNow = maxTime-7*86400000L+i*3600000L; 
		
		System.out.println(now);
		Interval_H yesterDayNow_interval_H = new Interval_H();//前一天
		Interval_H beforeYesterDayNow_interval_H = new Interval_H();//前两天
		Interval_H lastWeekNow_interval_H = new Interval_H();//上一周
		
		yesterDayNow_interval_H = h_testList.get(i-24);
		
		Map<String,Object> befoeYesterDayNowParmMap = new HashMap<>();
		befoeYesterDayNowParmMap.put("cst_id",cst_id);
		befoeYesterDayNowParmMap.put("time", befoeYesterDayNow);
		beforeYesterDayNow_interval_H = interval_HService.getAllByCstIdAndTime(befoeYesterDayNowParmMap);
		
		Map<String,Object> lastWeekNowParmMap = new HashMap<>();
		lastWeekNowParmMap.put("cst_id",cst_id);
		lastWeekNowParmMap.put("time", lastWeekNow);
		lastWeekNow_interval_H = interval_HService.getAllByCstIdAndTime(lastWeekNowParmMap);
		
		Interval_H interval_H = new Interval_H();
		interval_H.setCst_id(Integer.parseInt(cst_id));
		interval_H.setMdi_ts(now);
		interval_H.setCdate(DateUtil.convertLongToTime(now));
		interval_H.setH0(0.0);
		
		if(i>0){
			System.out.println(h_testList.get(i-1).getH0());
		}	
		if(i==0){
			//获取前一个小时的用水量
			double h1 = interval_HService.getH0ByTime(now-3600000L,cst_id);
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}else{
			double h1 = h_testList.get(i-1).getH0(); 
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}	
		if(i<2){
			//获取前两个小时的用水量
			double h2 = interval_HService.getH0ByTime(now-2*3600000L,cst_id);
			System.out.println("前两个小时"+h2);
			interval_H.setH2(h2);
		}else{
			double h2 = h_testList.get(i-2).getH0();
			System.out.println("前两个小时"+h2);
			interval_H.setH2(h2);
		}	
		//获取前一天这一小时的用水量
		interval_H.setH10(h_testList.get(i-24).getH0());
		//获取前一天这一小时后一小时的用水量
		double h11 = h_testList.get(i-23).getH0();
		interval_H.setH11(h11);
		//获取前一天这一小时前一小时的用水量
		if(i==24){
			double h12 =interval_HService.getH0ByTime(yesterDayNow_interval_H.getMdi_ts()-3600000L,cst_id);
			interval_H.setH12(h12);
		}else{
			double h12 = h_testList.get(i-25).getH0();
			interval_H.setH12(h12);
		}
		
		//获取前两天这一小时的用水量
		interval_H.setH20(beforeYesterDayNow_interval_H.getH0());
		//获取前两天这一小时后一小时的用水量
		double h21 = interval_HService.getH0ByTime(beforeYesterDayNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH21(h21);
		//获取前两天这一小时前一小时的用水量
		double h22 = interval_HService.getH0ByTime(beforeYesterDayNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH22(h22);
		//获取前一周这一小时的用水量
		interval_H.setH70(lastWeekNow_interval_H.getH0());
		//获取前一周这一小时后一小时的用水量
		double h71 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH71(h71);
		//获取前一周这一小时前一小时的用水量
		double h72 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH72(h72);
		
		h_testList.add(interval_H);			
}
	
	private void createAfter48HourTestList(int i,List<Interval_H> h_testList){
		//预测一小时
		long now = maxTime + i*3600000L;//当前时间，精确到小时
		long lastWeekNow = maxTime-7*86400000L+i*3600000L; 		
		System.out.println(now);
		Interval_H beforeYesterDayNow_interval_H = new Interval_H();//前两天
		Interval_H lastWeekNow_interval_H = new Interval_H();//上一周	
		
		beforeYesterDayNow_interval_H = h_testList.get(i-48);
		
		Map<String,Object> lastWeekNowParmMap = new HashMap<>();		
		lastWeekNowParmMap.put("cst_id",cst_id);
		lastWeekNowParmMap.put("time", lastWeekNow);
		lastWeekNow_interval_H = interval_HService.getAllByCstIdAndTime(lastWeekNowParmMap);
		
		Interval_H interval_H = new Interval_H();
		interval_H.setCst_id(Integer.parseInt(cst_id));
		interval_H.setMdi_ts(now);
		interval_H.setCdate(DateUtil.convertLongToTime(now));
		interval_H.setH0(0.0);
		
		if(i>0){
			System.out.println(h_testList.get(i-1).getH0());
		}	
		if(i==0){
			//获取前一个小时的用水量
			double h1 = interval_HService.getH0ByTime(now-3600000L,cst_id);
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}else{
			double h1 = h_testList.get(i-1).getH0(); 
			System.out.println("前一小时："+h1);
			interval_H.setH1(h1);
		}	
		if(i<2){
			//获取前两个小时的用水量
			double h2 = interval_HService.getH0ByTime(now-2*3600000L,cst_id);
			System.out.println("前两个小时"+h2);
			interval_H.setH2(h2);
		}else{
			double h2 = h_testList.get(i-2).getH0();
			System.out.println("前两个小时"+h2);
			interval_H.setH2(h2);
		}	
		//获取前一天这一小时的用水量
		interval_H.setH10(h_testList.get(i-24).getH0());
		//获取前一天这一小时后一小时的用水量
		double h11 = h_testList.get(i-25).getH0();
		interval_H.setH11(h11);
		//获取前一天这一小时前一小时的用水量
		double h12 = h_testList.get(i-23).getH0();
		interval_H.setH12(h12);
		//获取前两天这一小时的用水量
		interval_H.setH20(h_testList.get(i-48).getH0());
		//获取前两天这一小时后一小时的用水量
		double h21 = h_testList.get(i-47).getH0();
		interval_H.setH21(h21);
		//获取前两天这一小时前一小时的用水量
		if(i==48){
			double h22 = interval_HService.getH0ByTime(beforeYesterDayNow_interval_H.getMdi_ts()-3600000L,cst_id);
			interval_H.setH22(h22);
		}else{
			double h22 = h_testList.get(i-49).getH0();
			interval_H.setH22(h22);
		}
		
		//获取前一周这一小时的用水量
		interval_H.setH70(lastWeekNow_interval_H.getH0());
		//获取前一周这一小时后一小时的用水量
		double h71 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()+3600000L,cst_id);
		interval_H.setH71(h71);
		//获取前一周这一小时前一小时的用水量
		double h72 = interval_HService.getH0ByTime(lastWeekNow_interval_H.getMdi_ts()-3600000L,cst_id);
		interval_H.setH72(h72);
		
		h_testList.add(interval_H);			
}
	//预测一天的数据
	private void createOnedayTestList(double lastWeekWaterValue,List<New_Interval_D> testList){
		Map<String, Object> parmMap =new HashMap<>();
		parmMap.put("cst_id", cst_id);
		parmMap.put("mdi_ts", maxTime);
		New_Interval_D beforeDay = interval_DService.getNew_Interval_DByCstIdAndTs(parmMap);	
		New_Interval_D interval_D = new New_Interval_D();
		
		today = beforeDay.getMdi_ts() + 86400000L;//获取今天的时间，毫秒表示
		System.out.println(today);
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
	
	private void createBeyoundSevenTestList(int i,double lastWeekWaterValue,List<New_Interval_D> testList){
		today = today + 86400000L;
		System.out.println(today);
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
