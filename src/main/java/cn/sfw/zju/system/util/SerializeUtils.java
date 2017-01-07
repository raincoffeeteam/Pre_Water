package cn.sfw.zju.system.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.trees.M5P;

public class SerializeUtils {
	
	public static byte[] serialize(Object serialObj){
		byte[] objBytes=null;
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();  
	        ObjectOutputStream objOuts=new ObjectOutputStream(byteArray);
			objOuts.writeObject(serialObj);  
			objBytes = byteArray.toByteArray(); 
			byteArray.close();
			objOuts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return objBytes;
	}
	
	public static Object deSerialize(byte[] bytes){
		Object obj=null;
		try {
			ByteArrayInputStream bais=new ByteArrayInputStream(bytes);  
			ObjectInputStream ois=new ObjectInputStream(bais);  
			obj=ois.readObject(); 
			ois.close();
			bais.close();			
		} catch (Exception e) {
		}
		return obj;	
	}
	
	public static void main(String[] args) throws Exception {
		String[] options ={"-M", "16", "-N"};
		M5P m5p= new M5P();
		m5p.setOptions(options);
		try {
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		
		
	}
	
}
