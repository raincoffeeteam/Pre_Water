package cn.sfw.zju.system.vo;

public class Water {
	Integer csy_id;
	String cst_name;
	String mdi_ts;
	Double now;
	Double bef;
	public Integer getCsy_id() {
		return csy_id;
	}
	public void setCsy_id(Integer csy_id) {
		this.csy_id = csy_id;
	}
	public String getCst_name() {
		return cst_name;
	}
	public void setCst_name(String cst_name) {
		this.cst_name = cst_name;
	}

	public Double getNow() {
		return now;
	}
	public void setNow(Double now) {
		this.now = now;
	}
	public Double getBef() {
		return bef;
	}
	public void setBef(Double bef) {
		this.bef = bef;
	}
	public String getMdi_ts() {
		return mdi_ts;
	}
	public void setMdi_ts(String mdi_ts) {
		this.mdi_ts = mdi_ts;
	}
	
}
