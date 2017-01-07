package cn.sfw.zju.system.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import cn.sfw.zju.system.service.IntervalService;
import cn.sfw.zju.system.util.DateUtil;
import net.sf.json.JSONArray;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;

@Controller
@RequestMapping("/system/interval")
public class IntervalAction {
	
	private final static Log log = LogFactory.getLog(IntervalAction.class);
	
	@Autowired
	private IntervalService intervalService;
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return Message ���еĲ��Ե�(CST_ID,CST_NAME)
	 */
	@ResponseBody
	@RequestMapping(value = "/getAllCstId/", method = RequestMethod.POST)
	public Message getAllCstId(HttpServletRequest request,HttpServletResponse response) {
		Message message = new Message();
		List<Map<String, Object>> result = intervalService.getAllCstId();
		JSONArray array = JSONArray.fromObject(result);
		message.setData(array);
		message.setCode(ResponseCode.SUCCESS);
		return message;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return Message ���еĲ��Ե�(CST_ID,CST_NAME,timeInterval)
	 */
	@ResponseBody
	@RequestMapping(value = "/getAllById/", method = RequestMethod.POST)
	public Message getAllById(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,Object> map) {
		Message message = new Message();
		
		String cst_id = (String) map.get("cst_id");
		Long timeInterval=Long.valueOf(String.valueOf(map.get("timeInterval")))*60000;
		//��ȡ��վ����С��ʱ��
		Long mdi_ts = intervalService.getMinTimeById(cst_id);
		System.out.println(mdi_ts);
		Map<String, Object> parmMap=new HashMap<String, Object>();
		parmMap.put("cst_id", cst_id);
		parmMap.put("mdi_ts", mdi_ts);
		parmMap.put("timeInterval", timeInterval);
		//��ȡ��վ��ӿ�ʼ��������е�����(cst_id,cst_name,mdi_ts,now,bef)
		List<Map<String, Object>> result =intervalService.getAllById(parmMap);	
		try {
			M5P classifier= cartModel(createInstances(result));
			//Ԥ��δ��һ��
			Date date= new Date();
			List<Map<String, Object>> flist=createForcastDate(new Date(date.getYear(),date.getMonth(),date.getDate()), 1, timeInterval);
			Instances test= createInstances(flist);
			for(int i=0;i<test.size();i++){
				flist.get(i).replace("NOW",classifier.classifyInstance(test.get(i)) );
				System.out.println(flist.get(i).get("MDI_TS")+": "+flist.get(i).get("NOW"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result.size());
		
		return message;
	}
	
	public List<Map<String, Object>> createForcastDate(Date date,int length,long timeInterval){
		Date startDate=date;
		Date endDate= DateUtil.resetDate(startDate,length);
		System.out.println(startDate.toLocaleString());
		System.out.println(endDate.toLocaleString());
		Long n=(endDate.getTime()-startDate.getTime())/timeInterval;
		List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();
		for(int i=0;i<n;i++){
			Map<String, Object> map= new HashMap<String, Object>();
			map.put("MDI_TS", endDate.getTime()+i*timeInterval);
			map.put("NOW",0);
			map.put("BEF",0);
			list.add(map);
		}
		return list;
	}
	
	public Instances createInstances(List<Map<String, Object>> list){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		//M,D,W,IS_HOLIDAYS,R1,R2,R3,R7,A3,R
		Attribute mdi_ts = new Attribute("MDI_TS");
		Attribute now = new Attribute("NOW");
		Attribute bef = new Attribute("BEF");
		atts.add(mdi_ts);
		atts.add(now);
		atts.add(bef);
		//
		Instances df = new Instances("Data", atts, 0);
		for(int i=0;i<list.size();i++){
			Instance inst = new DenseInstance(3); 
			// Set instance's values for the attributes "length", "weight", and "position"
			inst.setValue(mdi_ts, Double.valueOf(String.valueOf(list.get(i).get("MDI_TS")))); 
			inst.setValue(now, Double.valueOf(String.valueOf(list.get(i).get("NOW"))));
			inst.setValue(bef, Double.valueOf(String.valueOf(list.get(i).get("BEF"))));
			// Set instance's dataset to be the dataset "race" 
			inst.setDataset(df); 
			df.add(inst);
		}
		df.setClassIndex(1);
		return df;
	}
	
	public static Instances boostrapSample(Instances data) {
        String[] options = {"-S","1","-Z","50"};
        Resample convert = new Resample();
        try {
            convert.setOptions(options);
            convert.setInputFormat(data);
            data = Filter.useFilter(data, convert);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
	
	/**
	 * Valid options are:
	 -N
	  Use unpruned tree/rules
	 
	 -U
	  Use unsmoothed predictions
	 
	 -R
	  Build regression tree/rule rather than a model tree/rule
	 
	 -M <minimum number of instances>
	  Set minimum number of instances per leaf
	  (default 4)
	 
	 -L
	  Save instances at the nodes in
	  the tree (for visualization purposes)
	  -s 
		�����������࣬�������ࣨC-SVC,nu-SVC�����ع飨epsilon-SVR,nu-SVR���Լ��ֲ����ƣ�one-class SVM����c-svc��c�ķ�Χ��1�������� 
		nu-svc��nu�ķ�Χ��0��1������nu�Ǵ��������ռ�������Ͻ磬֧��������ռ���е��½硣 
		-k 
		�˺������ͣ�����ʽ�����ԡ���˹��tanh������ 
		-d 
		degree���˺����е�degree����(��Զ���ʽ�˺���)(Ĭ��3) 
		-g 
		�˺����е�gamma��������(��Զ���ʽ/rbf/sigmoid�˺���)(Ĭ��1/ k) 
		-r 
		coef0���˺����е�coef0����(��Զ���ʽ/sigmoid�˺���)((Ĭ��0) 
		-c 
		cost������C-SVC��e -SVR��v-SVR�Ĳ���(��ʧ����)(Ĭ��1) 
		-n 
		nu������v-SVC��һ��SVM��v- SVR�Ĳ���(Ĭ��0.5) 
		-Z 
		����������ݽ���normalization��Ĭ���Ƿǿ��� 
		-J 
		����ȫ������numric��ʱ�򣬽��ж����Ʊ��� 
		-V 
		ȱʧֵ�Ƿ���Ĭ���ǿ��� 
		-p 
		p������e -SVR ����ʧ����p��ֵ(Ĭ��0.1) 
		-m 
		cachesize������cache�ڴ��С����MBΪ��λ(Ĭ��40) 
		-e 
		eps�������������ֹ�о�(Ĭ��0.001) 
		-h 
		shrinking���Ƿ�ʹ������ʽ��0��1(Ĭ��1) 
		-wi 
		weight�����õڼ���Ĳ���CΪweight*C(C-SVC�е�C)(Ĭ��1)
	 * @param data
	 * @throws Exception
	 */
	 public M5P cartModel(Instances data) throws Exception{
			M5P classifier =new M5P();
	        String[] options = {"-M","5","-R"};
	        classifier.setOptions(options);
	        classifier.buildClassifier(data);
	        Evaluation eval = new Evaluation(data);
	        //eval.evaluateModel(classifier, data);
			eval.crossValidateModel(classifier, data, 10, new Random(1));
			System.out.println(eval.toSummaryString("\nResult", false));
			//System.out.println(eval.toClassDetailsString());
			return classifier;
	 }
}
