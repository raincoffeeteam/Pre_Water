package cn.sfw.zju.system.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sfw.zju.system.vo.Interval_D;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class InstancesUtils {
	public static Instances createPreDayInstances(List<Map<String, Object>> list){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		//M,D,W,IS_HOLIDAYS,R1,R2,R3,R7,A3,R
		List<String> m_list = new ArrayList<String>(12);
		for(int i=1;i<=12;i++){
			if(i<10){
				m_list.add("0"+i);	
			}else{
				m_list.add(""+i);	
			}
			
		}
		List<String> d_list = new ArrayList<String>(31);
		for(int i=1;i<=31;i++){
			if(i<10){
				d_list.add("0"+i);	
			}else{
				d_list.add(""+i);	
			}
		}
		List<String> w_list = new ArrayList<String>(7);
		for(int i=1;i<=7;i++){
			w_list.add(""+i);
		}
		List<String> is_holidays_list = new ArrayList<String>(2);
		is_holidays_list.add("0");
		is_holidays_list.add("1");
		Attribute m = new Attribute("M",m_list);
		Attribute d = new Attribute("D",d_list);
		Attribute w = new Attribute("W",w_list);
		Attribute is_holidays = new Attribute("IS_HOLIDAYS",is_holidays_list);
		Attribute r1 = new Attribute("R1");
		Attribute r2 = new Attribute("R2");
		Attribute r3 = new Attribute("R3");
		Attribute r7 = new Attribute("R7");
		Attribute a3 = new Attribute("A3");
		Attribute r = new Attribute("R");
		atts.add(m);
		atts.add(d);
		atts.add(w);
		atts.add(is_holidays);
		atts.add(r1);
		atts.add(r2);
		atts.add(r3);
		atts.add(r7);
		atts.add(a3);
		atts.add(r);
		//create the instances
		Instances instances = new Instances("Data", atts, 0);
		for(int i=0;i<list.size();i++){
			Instance inst = new DenseInstance(10); 
			// Set instance's values for the attributes 
			inst.setValue(m, String.valueOf(list.get(i).get("M")));
			inst.setValue(d, String.valueOf(list.get(i).get("D")));
			inst.setValue(w, String.valueOf(list.get(i).get("W")));
			inst.setValue(is_holidays, String.valueOf(list.get(i).get("IS_HOLIDAYS")));
			inst.setValue(r1, Double.parseDouble(list.get(i).get("R1").toString()));
			inst.setValue(r2, Double.parseDouble(list.get(i).get("R2").toString()));
			inst.setValue(r3, Double.parseDouble(list.get(i).get("R3").toString()));
			inst.setValue(r7, Double.parseDouble(list.get(i).get("R7").toString()));
			inst.setValue(a3, Double.parseDouble(list.get(i).get("A3").toString()));
			inst.setValue(r, Double.parseDouble(list.get(i).get("R").toString())); 
			inst.setDataset(instances); 
			instances.add(inst);
		}
		instances.setClassIndex(9);
		return instances;
	}
	
	public static Instances createPreDayInstance(Interval_D interval_D){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		//M,D,W,IS_HOLIDAYS,R1,R2,R3,R7,A3,R
		List<String> m_list = new ArrayList<String>(12);
		for(int i=1;i<=12;i++){
			if(i<10){
				m_list.add("0"+i);	
			}else{
				m_list.add(""+i);	
			}
			
		}
		List<String> d_list = new ArrayList<String>(31);
		for(int i=1;i<=31;i++){
			if(i<10){
				d_list.add("0"+i);	
			}else{
				d_list.add(""+i);	
			}
		}
		List<String> w_list = new ArrayList<String>(7);
		for(int i=1;i<=7;i++){
			w_list.add(""+i);
		}
		List<String> is_holidays_list = new ArrayList<String>(2);
		is_holidays_list.add("0");
		is_holidays_list.add("1");
		Attribute m = new Attribute("M",m_list);
		Attribute d = new Attribute("D",d_list);
		Attribute w = new Attribute("W",w_list);
		Attribute is_holidays = new Attribute("IS_HOLIDAYS",is_holidays_list);
		Attribute r1 = new Attribute("R1");
		Attribute r2 = new Attribute("R2");
		Attribute r3 = new Attribute("R3");
		Attribute r7 = new Attribute("R7");
		Attribute a3 = new Attribute("A3");
		Attribute r = new Attribute("R");
		atts.add(m);
		atts.add(d);
		atts.add(w);
		atts.add(is_holidays);
		atts.add(r1);
		atts.add(r2);
		atts.add(r3);
		atts.add(r7);
		atts.add(a3);
		atts.add(r);
		//create the instances
		Instances instances = new Instances("Data", atts, 0);

			Instance inst = new DenseInstance(10); 
			// Set instance's values for the attributes 
			inst.setValue(m, String.valueOf(interval_D.getM()));
			inst.setValue(d, String.valueOf(interval_D.getD()));
			inst.setValue(w, String.valueOf(interval_D.getW()));
			inst.setValue(is_holidays, String.valueOf(interval_D.getIs_holidays()));
			inst.setValue(r1, Double.parseDouble(interval_D.getR1().toString()));
			inst.setValue(r2, Double.parseDouble(interval_D.getR2().toString()));
			inst.setValue(r3, Double.parseDouble(interval_D.getR3().toString()));
			inst.setValue(r7, Double.parseDouble(interval_D.getR7().toString()));
			inst.setValue(a3, Double.parseDouble(interval_D.getA3().toString()));
			inst.setValue(r, Double.parseDouble(interval_D.getR().toString())); 
			inst.setDataset(instances); 
			instances.add(inst);

		instances.setClassIndex(9);
		return instances;
	}
}
