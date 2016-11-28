package cn.sfw.zju.common;

import java.util.Date;

//import com.ibm.crl.uaqm.system.vo.AnalysisResult;



public class Message extends BaseVO{
	private static final long serialVersionUID = 1L;
	private String code;
	private String msg;
	private String key;
	private String[] params;
	private String timestamp;
	private long execTime;
	private Object data;
	/*private AnalysisResult analysisResult;
	
	public AnalysisResult getAnalysisResult() {
		return analysisResult;
	}
	public void setAnalysisResult(AnalysisResult analysisResult) {
		this.analysisResult = analysisResult;
	}*/
	public long getExecTime() {
		return execTime;
	}
	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getKey() {
		return key;
	}
	public void setErrorMsg(String msg) {
		this.code=Constant.FAILED;
		this.timestamp=Constant.SDF.format(new Date());
		this.msg=msg;
	}
	public void setSuccessMsg(String msg) {
		this.code=Constant.SUCCESS;
		this.timestamp=Constant.SDF.format(new Date());
		this.msg=msg;
	}
	public void setErrorKey(String key) {
		this.key = key;
		this.code=Constant.FAILED;
		this.timestamp=Constant.SDF.format(new Date());
	}
	public void setErrorKey(String key,String[] params) {
		this.key = key;
		this.params = params;
		this.code=Constant.FAILED;
		this.timestamp=Constant.SDF.format(new Date());
	}
	public void setSuccessKey(String key) {
		this.key = key;
		this.code=Constant.SUCCESS;
		this.timestamp=Constant.SDF.format(new Date());
	}
	public void setSuccessKey(String key,String[] params) {
		this.key = key;
		this.params = params;
		this.code=Constant.SUCCESS;
		this.timestamp=Constant.SDF.format(new Date());
	}
	public String[] getParams() {
		return params;
	}
	public void setParams(String[] params) {
		this.params = params;
	}
	
}
