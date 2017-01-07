package cn.sfw.zju.system.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import cn.sfw.zju.system.service.Interval_DService;
import cn.sfw.zju.system.service.New_Interval_DService;
import cn.sfw.zju.system.util.DateUtil;
import cn.sfw.zju.system.util.InstancesUtils;
import cn.sfw.zju.system.util.WekaUtils;
import cn.sfw.zju.system.vo.Interval_H;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.meta.CVParameterSelection;
import weka.classifiers.trees.M5P;
import weka.core.Instances;
import weka.core.Utils;

@Controller
@RequestMapping("/system/modelMana")
public class ModelManaAction {

	private final static Log log = LogFactory.getLog(ModelManaAction.class);
	
	@Autowired
	private New_Interval_DService ndService;
	@Autowired
	private Interval_DService dService;
	
	private WekaUtils wekaUtils=WekaUtils.getInstance();
	
	@ResponseBody
	@RequestMapping(value = "/modelManage/", method = RequestMethod.POST)
	public Message createModel(HttpServletRequest request,HttpServletResponse response ,@RequestBody Map<String,Object> map ){
		Message message= new Message();
		
		//解析map
		String cst_id =(String) map.get("cst_id");
		Long beginDate=DateUtil.convertTimeToLong((String)map.get("beginDate"));
		Long endDate=DateUtil.convertTimeToLong((String)map.get("endDate"));
		String algorithm=(String)map.get("algorithm");
		String time= (String)map.get("time");
		
		//构造查询map
		Map<String, Object> parmMap =new HashMap<>(); 
		parmMap.put("cst_id", cst_id);
		parmMap.put("beginDate", beginDate);
		parmMap.put("endDate", endDate);
		
		Classifier classifier=null;
		String[] op = null;
		if(time.equals("day")){
			List<New_Interval_D> list=ndService.getByCstIdAndTime(parmMap);
			Instances train=InstancesUtils.createInstancesFromNID(list);
			//处理数据	
			try {
				train = wekaUtils.normalizeV(wekaUtils.standardizeV(train));
			} catch (Exception e1) {
				log.error("instances 数据处理异常");
				e1.printStackTrace();
			}
			//生成模型
			CVParameterSelection cvParameterSelection = new CVParameterSelection();
			try {
				if(algorithm.equals("tree")){
					//参数训练
					String[] options ={"-M", "16", "-N"};
					classifier=wekaUtils.getM5PClassifer(train, options);
					cvParameterSelection.setClassifier(classifier);					
					//cvParameterSelection.addCVParameter("M 1 20 20");					
					cvParameterSelection.setNumFolds(10);
					cvParameterSelection.buildClassifier(train);
					log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
					M5P m5p= wekaUtils.getM5PClassifer(train, cvParameterSelection.getBestClassifierOptions());					
					//获取结果信息
					op=m5p.getOptions();
					classifier=m5p;
					
				}else if (algorithm.equals("smoreg")){	
					//参数训练
					String[] options={"-C","0.5","-N","0"};
					classifier= wekaUtils.getSMOregClassifer(train, options);
					cvParameterSelection.setClassifier(classifier);
					//cvParameterSelection.addCVParameter("C 0.1 2 20");
					cvParameterSelection.setNumFolds(5);
					cvParameterSelection.buildClassifier(train);
					log.info("最优参数："+Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));
					SMOreg smOreg=wekaUtils.getSMOregClassifer(train, cvParameterSelection.getBestClassifierOptions());
					
					//获取结果信息
					op=smOreg.getOptions();
					classifier=smOreg;
					
				}else if(algorithm.equals("bp")){
					//参数训练
					cvParameterSelection= new CVParameterSelection();
				    String[] options={};
				    classifier= wekaUtils.getBPClassifer(train, options);
				    cvParameterSelection.setClassifier(classifier);
					cvParameterSelection.setNumFolds(10);
					cvParameterSelection.buildClassifier(train);
					System.out.println(Utils.joinOptions(cvParameterSelection.getBestClassifierOptions()));				
					MultilayerPerceptron bp= wekaUtils.getBPClassifer(train, cvParameterSelection.getBestClassifierOptions());
					
					//获取结果信息
					op=bp.getOptions();
					classifier=bp;
					
				}else{
					log.info("没有该算法");
				}
			} catch (Exception e) {
				log.error("训练模型失败");
			}
		}else if(time.equals("hour")){
			List<Interval_H> list=null;
		}else if(time.equals("min")){
			
		}else{
			return message;
		}
		
		Map<String, Object> model= new HashMap<String, Object>();
		
		
		model.put("classifier", classifier);
		model.put("options", op);
		message.setData(model);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
	
}
