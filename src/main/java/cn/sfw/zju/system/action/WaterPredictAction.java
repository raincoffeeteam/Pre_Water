package cn.sfw.zju.system.action;

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
import cn.sfw.zju.common.util.DateUtil;
import cn.sfw.zju.system.service.Interval_DService;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.Interval_D;
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
	 * @param map
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/predictWater/", method = RequestMethod.POST)
	public Message predictWater(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map) {
		Message message = new Message();
		
		String cst_id =(String) map.get("cst_id");
		
		//查询该测点的数据起始和终止日期，获取train instances
		Map<String, Object> dateMap =interval_DService.getMaxDateAndMinDateByCstId(cst_id); 
		dateMap.put("cst_id", cst_id);
		List<Map<String, Object>> list= interval_DService.getByCstIdAndTime(dateMap);
		Instances train=InstancesUtils.createPreDayInstances(list);
		
		//获取时间间隔timeInterval(假设暂时指定天)
		int timeInterval=(int) map.get("timeInterval");
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
		
		//获取时间间隔forcastDays
		int forcastDays=(int) map.get("forcastDays");
		String date = null;
		
		//定义预测结果
		List<Map<String, Object>> resultList = new ArrayList<>();
		
		//进行预测
		for(int i=0;i<forcastDays;i++){
			Map<String, Object> parmMap =new HashMap<>();
			parmMap.put("cst_id", cst_id);
			//@undo 
			parmMap.put("mdi_ts", 1474387200000L);
			Interval_D beforeDay= interval_DService.getInterval_DByCstIdAndTs(parmMap);			
			Interval_D interval_D= new Interval_D();
			interval_D.setCdate(date);
			interval_D.setM(DateUtil.month(date));
			interval_D.setD(DateUtil.day(date));
			interval_D.setW(DateUtil.week(date));
			interval_D.setW(DateUtil.is_holiday(date));
			interval_D.setR1(beforeDay.getR());
			interval_D.setR2(beforeDay.getR1());
			interval_D.setR3(beforeDay.getR2());
			interval_D.setR7(beforeDay.getR());//@UNDO
			interval_D.setA3((beforeDay.getR()+beforeDay.getR1()+beforeDay.getR2())/3);
			interval_D.setR(0.0);
			Instances test= InstancesUtils.createPreDayInstance(interval_D);
			try {
				double predictValue=classifier.classifyInstance(test.instance(0));
				Map<String, Object> resultmap= new HashMap<>();
				resultmap.put("cst_id",cst_id);
				resultmap.put("cDate", date);
				resultmap.put("predictValue", predictValue);
				resultList.add(resultmap);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("error");
			}
		}
		message.setData(resultList);
		message.setCode("SUCCESS");
		return message;
	}
	
}
