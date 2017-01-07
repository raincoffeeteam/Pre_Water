package cn.sfw.zju.system.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.Interval_H;
import cn.sfw.zju.system.vo.New_Interval_D;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class InstancesUtils {
	
	/**
	 * 将一个New_Interval_Dlist转换为instances
	 * @param list
	 * @return
	 */
	public static Instances createInstancesFromNID(List<New_Interval_D> list){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		
		Attribute m1=new Attribute("M1");
		Attribute m2=new Attribute("M2");
		Attribute m3=new Attribute("M3");
		Attribute m4=new Attribute("M4");
		Attribute m5=new Attribute("M5");
		Attribute m6=new Attribute("M6");
		Attribute m7=new Attribute("M7");
		Attribute m8=new Attribute("M8");
		Attribute m9=new Attribute("M9");
		Attribute m10=new Attribute("M10");
		Attribute m11=new Attribute("M11");
		Attribute m12=new Attribute("M12");
		
		Attribute d1=new Attribute("D1");
		Attribute d2=new Attribute("D2");
		Attribute d3=new Attribute("D3");
		Attribute d4=new Attribute("D4");
		Attribute d5=new Attribute("D5");
		Attribute d6=new Attribute("D6");
		Attribute d7=new Attribute("D7");
		Attribute d8=new Attribute("D8");
		Attribute d9=new Attribute("D9");
		Attribute d10=new Attribute("D10");
		Attribute d11=new Attribute("D11");
		Attribute d12=new Attribute("D12");
		Attribute d13=new Attribute("D13");
		Attribute d14=new Attribute("D14");
		Attribute d15=new Attribute("D15");
		Attribute d16=new Attribute("D16");
		Attribute d17=new Attribute("D17");
		Attribute d18=new Attribute("D18");
		Attribute d19=new Attribute("D19");
		Attribute d20=new Attribute("D20");
		Attribute d21=new Attribute("D21");
		Attribute d22=new Attribute("D22");
		Attribute d23=new Attribute("D23");
		Attribute d24=new Attribute("D24");
		Attribute d25=new Attribute("D25");
		Attribute d26=new Attribute("D26");
		Attribute d27=new Attribute("D27");
		Attribute d28=new Attribute("D28");
		Attribute d29=new Attribute("D29");
		Attribute d30=new Attribute("D30");
		Attribute d31=new Attribute("D31");
		
		Attribute w1=new Attribute("W1");
		Attribute w2=new Attribute("W2");
		Attribute w3=new Attribute("W3");
		Attribute w4=new Attribute("W4");
		Attribute w5=new Attribute("W5");
		Attribute w6=new Attribute("W6");
		Attribute w7=new Attribute("W7");
		
		Attribute is_holidays= new Attribute("IS_HOLIDAYS");
		Attribute not_holidays= new Attribute("NOT_HOLIDAYS");
		
		Attribute r1 = new Attribute("R1");
		Attribute r2 = new Attribute("R2");
		Attribute r3 = new Attribute("R3");
		Attribute r7 = new Attribute("R7");
		Attribute a3 = new Attribute("A3");
		Attribute r = new Attribute("R");
		
		atts.add(m1);
		atts.add(m2);
		atts.add(m3);
		atts.add(m4);
		atts.add(m5);
		atts.add(m6);
		atts.add(m7);
		atts.add(m8);
		atts.add(m9);
		atts.add(m10);
		atts.add(m11);
		atts.add(m12);
			
		atts.add(d1);
		atts.add(d2);
		atts.add(d3);
		atts.add(d4);
		atts.add(d5);
		atts.add(d6);
		atts.add(d7);
		atts.add(d8);
		atts.add(d9);
		atts.add(d10);
		atts.add(d11);
		atts.add(d12);
		atts.add(d13);
		atts.add(d14);
		atts.add(d15);
		atts.add(d16);
		atts.add(d17);
		atts.add(d18);
		atts.add(d19);
		atts.add(d20);
		atts.add(d21);
		atts.add(d22);
		atts.add(d23);
		atts.add(d24);
		atts.add(d25);
		atts.add(d26);
		atts.add(d27);
		atts.add(d28);
		atts.add(d29);
		atts.add(d30);
		atts.add(d31);
		
		atts.add(w1);
		atts.add(w2);
		atts.add(w3);
		atts.add(w4);
		atts.add(w5);
		atts.add(w6);
		atts.add(w7);
		
		atts.add(is_holidays);
		atts.add(not_holidays);
		
		atts.add(r1);
		atts.add(r2);
		atts.add(r3);
		atts.add(r7);
		atts.add(a3);
		atts.add(r);
		Instances instances = new Instances("Data", atts, 0);
		for(int i=0;i<list.size();i++){
			Instance inst = new DenseInstance(58);
			
			inst.setValue(m1, list.get(i).getM1());
			inst.setValue(m2, list.get(i).getM2());
			inst.setValue(m3, list.get(i).getM3());
			inst.setValue(m4, list.get(i).getM4());
			inst.setValue(m5, list.get(i).getM5());
			inst.setValue(m6, list.get(i).getM6());
			inst.setValue(m7, list.get(i).getM7());
			inst.setValue(m8, list.get(i).getM8());
			inst.setValue(m9, list.get(i).getM9());
			inst.setValue(m10, list.get(i).getM10());
			inst.setValue(m11, list.get(i).getM11());
			inst.setValue(m12, list.get(i).getM12());
			
			inst.setValue(d1, list.get(i).getD1());
			inst.setValue(d2, list.get(i).getD2());
			inst.setValue(d3, list.get(i).getD3());
			inst.setValue(d4, list.get(i).getD4());
			inst.setValue(d5, list.get(i).getD5());
			inst.setValue(d6, list.get(i).getD6());
			inst.setValue(d7, list.get(i).getD7());
			inst.setValue(d8, list.get(i).getD8());
			inst.setValue(d9, list.get(i).getD9());
			inst.setValue(d10, list.get(i).getD10());			
			inst.setValue(d11, list.get(i).getD11());
			inst.setValue(d12, list.get(i).getD12());
			inst.setValue(d13, list.get(i).getD13());
			inst.setValue(d14, list.get(i).getD14());
			inst.setValue(d15, list.get(i).getD15());
			inst.setValue(d16, list.get(i).getD16());
			inst.setValue(d17, list.get(i).getD17());
			inst.setValue(d18, list.get(i).getD18());
			inst.setValue(d19, list.get(i).getD19());
			inst.setValue(d20, list.get(i).getD20());
			inst.setValue(d21, list.get(i).getD21());
			inst.setValue(d22, list.get(i).getD22());
			inst.setValue(d23, list.get(i).getD23());
			inst.setValue(d24, list.get(i).getD24());
			inst.setValue(d25, list.get(i).getD25());
			inst.setValue(d26, list.get(i).getD26());
			inst.setValue(d27, list.get(i).getD27());
			inst.setValue(d28, list.get(i).getD28());
			inst.setValue(d29, list.get(i).getD29());
			inst.setValue(d30, list.get(i).getD30());
			inst.setValue(d31, list.get(i).getD31());
			
			inst.setValue(w1, list.get(i).getW1());
			inst.setValue(w2, list.get(i).getW2());
			inst.setValue(w3, list.get(i).getW3());
			inst.setValue(w4, list.get(i).getW4());
			inst.setValue(w5, list.get(i).getW5());
			inst.setValue(w6, list.get(i).getW6());
			inst.setValue(w7, list.get(i).getW7());
			
			inst.setValue(is_holidays, list.get(i).getIs_holidays());
			inst.setValue(not_holidays, list.get(i).getNot_holidays());
			
			inst.setValue(r1, list.get(i).getR1());
			inst.setValue(r2, list.get(i).getR2());
			inst.setValue(r3, list.get(i).getR3());
			inst.setValue(r7, list.get(i).getR7());
			inst.setValue(a3, list.get(i).getA3());
			inst.setValue(r, list.get(i).getR()); 
			inst.setDataset(instances);
			instances.add(inst);
			
		}
		instances.setClassIndex(57);
		return instances;
	}
	
	
	
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
	
	
	public static Instances createInstancesFromIH(List<Interval_H> list){
		ArrayList<Attribute> atts = new ArrayList<Attribute>();		
		Attribute H0=new Attribute("H0");
		Attribute H1=new Attribute("H1");
		Attribute H2=new Attribute("H2");
		Attribute H10=new Attribute("H10");
		Attribute H11=new Attribute("H11");
		Attribute H12=new Attribute("H12");
		Attribute H20=new Attribute("H20");
		Attribute H21=new Attribute("H21");
		Attribute H22=new Attribute("H22");
		Attribute H70=new Attribute("H70");
		Attribute H71=new Attribute("H71");
		Attribute H72=new Attribute("H72");
		
		atts.add(H0);
		atts.add(H1);
		atts.add(H2);		
		atts.add(H10);
		atts.add(H11);
		atts.add(H12);		
		atts.add(H20);
		atts.add(H21);
		atts.add(H22);		
		atts.add(H70);
		atts.add(H71);
		atts.add(H72);
		
		Instances instances = new Instances("Data", atts, 0);
		for(int i=0;i<list.size();i++){
			Instance inst = new DenseInstance(12);
			inst.setValue(H0, list.get(i).getH0());
			inst.setValue(H1, list.get(i).getH1());
			inst.setValue(H2, list.get(i).getH2());
			
			inst.setValue(H10, list.get(i).getH10());
			inst.setValue(H11, list.get(i).getH11());
			inst.setValue(H12, list.get(i).getH12());
			
			inst.setValue(H20, list.get(i).getH20());
			inst.setValue(H21, list.get(i).getH21());
			inst.setValue(H22, list.get(i).getH22());
			
			inst.setValue(H70, list.get(i).getH70());
			inst.setValue(H71, list.get(i).getH71());
			inst.setValue(H72, list.get(i).getH72());
			inst.setDataset(instances);
			instances.add(inst);
		}
		instances.setClassIndex(0);
		return instances;		
	}
}
