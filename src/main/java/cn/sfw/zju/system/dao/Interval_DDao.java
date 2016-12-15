package cn.sfw.zju.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.sfw.zju.system.vo.Interval_D;

@Repository
public interface Interval_DDao {
	/**
	 * @param map{cst_id,beginDate,endDate}
	 * @return
	 */
	public List<Map<String, Object>> getByCstIdAndTime(Map<String, Object> map);
	
	public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id);
	
	public Interval_D getInterval_DByCstIdAndTs(Map<String, Object> map);
	
	public List<Map<String,Object>> getAllByCstId(String cst_id); //通过id获取数据
	
	public long getMaxTimeByCstId(String cst_id); //通过id获取当前最大时间
	public double getRByTime(long time,String cst_id);//通过时间获取用水量
	
	public void autoSave(Long t);
	
}
