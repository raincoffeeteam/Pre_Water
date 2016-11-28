package cn.sfw.zju.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import cn.sfw.zju.common.Constant;

public class ModelUtil {
	static Properties properties=null;
	static{
		try {
//			FileInputStream fis = new FileInputStream("F:\\workspace\\work\\UAQM_DP\\v3\\models.properties");
			FileInputStream fis = new FileInputStream(ModelUtil.class.getClassLoader().getResource("").getPath()+"properties/models.properties");
			properties=new Properties();
			properties.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String,String> getModels(){
		Map<String,String> map = new HashMap<String, String>();
		
		for ( Object o : properties.keySet()) {
			map.put(o.toString().trim(), properties.getProperty(o.toString()).trim());
		}
		
		return map;		
	}
//	public static String getDefaultModel(){
//		return (String) properties.get("DEFAULT_MODEL");
//	}
	
	public static void main(String[] args) {
		System.out.println(Constant.MODELS);
	}
}
