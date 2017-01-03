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
import cn.sfw.zju.common.util.DateUtil;
import cn.sfw.zju.system.service.Interval_DService;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.classifiers.Classifier;
import weka.classifiers.meta.CVParameterSelection;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;


@Controller
@RequestMapping("/system/modelEvalAction")
public class ModelEvalAction {
	
	private final static Log log = LogFactory.getLog(ModelEvalAction.class);
	@Autowired
	private Interval_DService interval_DService;
	
	@Autowired
	private New_Interval_DService dService;
	
	private WekaUtils wekaUtils=WekaUtils.getInstance();
	
	@ResponseBody
	@RequestMapping(value = "/modelEvalution/", method = RequestMethod.POST)
	public Message modelEvalution(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map) {
		Message message = new Message();
		
		String cst_id =(String) map.get("cst_id");
		
		//查询该测点的数据起始日期，获取train instances
		Map<String, Object> dateMap =interval_DService.getMaxDateAndMinDateByCstId(cst_id); 
		dateMap.put("cst_id", cst_id);
		List<New_Interval_D> list=dService.getByCstIdAndTime(dateMap);
		Instances train=InstancesUtils.createInstancesFromNID(list);
		
		//根据输入的参数获取预测日期。（该日期小于已有数据最大日期值）
		Long beginDate=DateUtil.convertTimeToLong((String)map.get("beginDate"));
		Long endDate=DateUtil.convertTimeToLong((String)map.get("endDate"));
		Map<String, Object> parmMap =new HashMap<>(); 
		parmMap.put("cst_id", cst_id);
		parmMap.put("beginDate", beginDate);
		parmMap.put("endDate", endDate);
		List<New_Interval_D> testList=dService.getByCstIdAndTime(parmMap);
		Instances test=InstancesUtils.createInstancesFromNID(testList);
		
		//处理数据	
		try {
			train = wekaUtils.normalizeV(wekaUtils.standardizeV(train));
			test = wekaUtils.normalizeV(wekaUtils.standardizeV(test));
		} catch (Exception e1) {
			log.error("instances 数据处理异常");
			e1.printStackTrace();
		}		
		//算法选择(获取一个list对象)
		List<String> algorithm =(List<String>) map.get("algorithm");
		
		//模型训练
		List<Map<String,Classifier >> modelList= new ArrayList<>();
		for(String s :algorithm){
			Classifier classifier=null;
			CVParameterSelection cvParameterSelection = new CVParameterSelection();
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
			Map<String,Classifier> classifierMap =  new HashMap<>();
			classifierMap.put(s, classifier);
			modelList.add(classifierMap);
		}
		//定义预测结果。
		List<Map<String, Object>> resultList = new ArrayList<>();
				
		//进行预测。
		double sum=test.numInstances();
		NumberFormat fmt = NumberFormat.getPercentInstance();  
        fmt.setMaximumFractionDigits(2);//最多两位百分小数，如25.23%  
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
		message.setCode("SUCCESS");
		return message;
	}
}
