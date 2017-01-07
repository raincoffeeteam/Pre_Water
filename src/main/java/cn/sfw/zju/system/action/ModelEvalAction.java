package cn.sfw.zju.system.action;

import java.text.NumberFormat;
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
import cn.sfw.zju.system.vo.Interval_H;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Instances;
import weka.core.Utils;


@Controller
@RequestMapping("/system/modelEval")
public class ModelEvalAction {
	
	private final static Log log = LogFactory.getLog(ModelEvalAction.class);
	
	@Autowired
	private Interval_HService interval_HService;
	
	@Autowired
	private New_Interval_DService interval_DService;
	
	private WekaUtils wekaUtils=WekaUtils.getInstance();
	
	@ResponseBody
	@RequestMapping(value = "/modelEvalution/", method = RequestMethod.POST)
	public Message modelEvalution(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map) {
		Message message = new Message();
		String time =(String) map.get("timeInterval");
		if(time.equals("day")){
			message=modelEvalutionByDay(map);
		}else if(time.equals("hour")){
			message=modelEvalutionByHour(map);
		}else{
			log.error("no support");
		}
		return message;	
	}
	
	
	private Message modelEvalutionByDay(final Map<String,Object> map){
		Message message = new Message();
		
		//get train instances
		String cst_id =(String) map.get("cst_id");
		Map<String, Object> dateMap =interval_DService.getMaxDateAndMinDateByCstId(cst_id);
		dateMap.put("cst_id", cst_id);
		List<New_Interval_D> list=interval_DService.getByCstIdAndTime(dateMap);
		Instances train=InstancesUtils.createInstancesFromNID(list);
		
		//get test instances
		Long beginDate=DateUtil.convertTimeToLong((String)map.get("beginDate"));
		Long endDate=DateUtil.convertTimeToLong((String)map.get("endDate"));
		Map<String, Object> parmMap =new HashMap<>(); 
		parmMap.put("cst_id", cst_id);
		parmMap.put("beginDate", beginDate);
		parmMap.put("endDate", endDate);
		List<New_Interval_D> testList=interval_DService.getByCstIdAndTime(parmMap);
		Instances test=InstancesUtils.createInstancesFromNID(testList);
		
		//deal instances
		try {
			train = wekaUtils.normalizeV(wekaUtils.standardizeV(train));
			test = wekaUtils.normalizeV(wekaUtils.standardizeV(test));
		} catch (Exception e) {
			log.error("instances 数据处理异常");
			e.printStackTrace();
		}
		
		//create model
		List<String> algorithm =(List<String>) map.get("algorithm");
		List<Map<String,Classifier >> modelList= createModel(algorithm, train);
		
		//predict
		List<Map<String, Object>> resultList = new ArrayList<>();
		double sum=test.numInstances();
		NumberFormat fmt = NumberFormat.getPercentInstance();  
		fmt.setMaximumFractionDigits(2); 
		for(int i=0;i<sum;i++){
			Map<String, Object>  resultMap= new HashMap<>();
			resultMap.put("id",cst_id);
			resultMap.put("name",cst_id);
			resultMap.put("time", testList.get(i).getCdate());
			resultMap.put("actualValue",testList.get(i).getR());
			double predictValue;
			double actualValue;
			String deviation;
			for(int j=0;j<algorithm.size();j++){
				Classifier classifier=modelList.get(j).get(algorithm.get(j));
				try {
					predictValue=classifier.classifyInstance(test.instance(i));
					actualValue=test.instance(i).classValue();
					deviation=fmt.format((predictValue-actualValue)/actualValue);							
					resultMap.put(algorithm.get(j)+"_predictValue",predictValue);
					resultMap.put(algorithm.get(j)+"_deviation",deviation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			resultList.add(resultMap);
		}
		message.setData(resultList);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
	private Message modelEvalutionByHour(final Map<String,Object> map){
		Message message = new Message();
		
		//get train instances
		String cst_id =(String) map.get("cst_id");
		Map<String, Object> dateMap =interval_HService.getMaxDateAndMinDateByCstId(cst_id);
		dateMap.put("cst_id", cst_id);
		List<Interval_H> list=interval_HService.getByCstIdAndTime(dateMap);
		Instances train=InstancesUtils.createInstancesFromIH(list);
		
		//get test instances
		Long beginDate=DateUtil.convertTimeToLong((String)map.get("beginDate"));
		Long endDate=DateUtil.convertTimeToLong((String)map.get("endDate"));
		Map<String, Object> parmMap =new HashMap<>(); 
		parmMap.put("cst_id", cst_id);
		parmMap.put("beginDate", beginDate);
		parmMap.put("endDate", endDate);
		List<Interval_H> testList=interval_HService.getByCstIdAndTime(parmMap);
		Instances test=InstancesUtils.createInstancesFromIH(testList);
		
		//deal instances
		try {
			train = wekaUtils.normalizeV(wekaUtils.standardizeV(train));
			test = wekaUtils.normalizeV(wekaUtils.standardizeV(test));
		} catch (Exception e) {
			log.error("instances 数据处理异常");
			e.printStackTrace();
		}
		
		//create model
		List<String> algorithm =(List<String>) map.get("algorithm");
		List<Map<String,Classifier >> modelList= createModel(algorithm, train);
		
		//predict
		List<Map<String, Object>> resultList = new ArrayList<>();
		double sum=test.numInstances();
		NumberFormat fmt = NumberFormat.getPercentInstance();  
		fmt.setMaximumFractionDigits(2); 
		for(int i=0;i<sum;i++){
			Map<String, Object>  resultMap= new HashMap<>();
			resultMap.put("id",cst_id);
			resultMap.put("name",cst_id);
			resultMap.put("time", testList.get(i).getCdate());
			resultMap.put("actualValue",testList.get(i).getH0());
			double predictValue;
			double actualValue;
			String deviation;
			for(int j=0;j<algorithm.size();j++){
				Classifier classifier=modelList.get(j).get(algorithm.get(j));
				try {
					predictValue=classifier.classifyInstance(test.instance(i));
					actualValue=test.instance(i).classValue();
					deviation=fmt.format((predictValue-actualValue)/actualValue);							
					resultMap.put(algorithm.get(j)+"_predictValue",predictValue);
					resultMap.put(algorithm.get(j)+"_deviation",deviation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
			resultList.add(resultMap);
		}
		message.setData(resultList);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
	private	List<Map<String,Classifier >> createModel(List<String> algorithm,Instances train){

		List<Map<String,Classifier >> modelList= new ArrayList<>();
		for(String s :algorithm){
			Classifier classifier=null;
			CVParameterSelection cvParameterSelection = new CVParameterSelection();
			try {
				if(s.equals("tree")){
					String[] options ={"-M", "0.2", "-N"};
					classifier=wekaUtils.getM5PClassifer(train, options);
					cvParameterSelection.setClassifier(classifier);
					cvParameterSelection.addCVParameter("M 1 10 10");					
					cvParameterSelection.setNumFolds(5);
					cvParameterSelection.buildClassifier(train);
					log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
					classifier= wekaUtils.getM5PClassifer(train, cvParameterSelection.getBestClassifierOptions());
					
				}else if (s.equals("smoreg")){				
					String[] options={"-C","0.5","-N","0"};
					classifier= wekaUtils.getSMOregClassifer(train, options);
					cvParameterSelection.setClassifier(classifier);
					//cvParameterSelection.addCVParameter("C 0.1 2 20");
					//cvParameterSelection.setNumFolds(5);
					cvParameterSelection.buildClassifier(train);
					log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
					classifier=wekaUtils.getSMOregClassifer(train, cvParameterSelection.getBestClassifierOptions());
					
				}else if(s.equals("bp")){
					cvParameterSelection= new CVParameterSelection();
					String[] options={};
					classifier= wekaUtils.getBPClassifer(train, options);
					cvParameterSelection.setClassifier(classifier);
					cvParameterSelection.setNumFolds(5);
					cvParameterSelection.buildClassifier(train);
					System.out.println(Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));				
					classifier= wekaUtils.getBPClassifer(train, cvParameterSelection.getBestClassifierOptions());
				}else{
					log.info("没有该算法");
				}
			} catch (Exception e) {
				log.error("训练模型失败");
			}
			Map<String,Classifier> classifierMap =  new HashMap<>();
			classifierMap.put(s, classifier);
			modelList.add(classifierMap);
		}
		return modelList;
		
	}
	
}
