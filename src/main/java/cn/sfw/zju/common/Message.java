package cn.sfw.zju.common;

import java.util.Date;

//import com.ibm.crl.uaqm.system.vo.AnalysisResult;



public class Message{
	private static final long serialVersionUID = 1L;
	private ResponseCode code;
	private String msg;
	private Object data;

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public ResponseCode getCode() {
		return code;
	}
	public void setCode(ResponseCode code) {
		this.code = code;
	}
	
}
