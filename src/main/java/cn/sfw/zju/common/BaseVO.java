package cn.sfw.zju.common;

import java.io.Serializable;
import java.lang.reflect.Field;

public class BaseVO implements Serializable{
	private static final long serialVersionUID = -1144371510766199741L;
	
	@Override
	public String toString() {
		Object obj = this;
		Class<? extends Object> class1 = obj.getClass();
		Field[] fields = class1.getDeclaredFields();
		StringBuffer sbf = new StringBuffer();
		sbf.append("\n" + class1.getName() + "\n");
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				sbf.append("[" + field.getName() + "="
							+ field.get(obj) + "]\n");
			} catch (Exception e) {
				System.err.println("Get " + class1.getName() + "'s value error!");
				e.printStackTrace();
			}
		}
		return sbf.toString();
	}
	
}
