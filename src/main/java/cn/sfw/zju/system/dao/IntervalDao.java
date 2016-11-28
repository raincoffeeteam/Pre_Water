package cn.sfw.zju.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IntervalDao {
	/**
	 * 获取所有观测点的id和name
	 * @return
	 */
	public List<Map<String, Object>> getAllCstId();
	
	/**
	 * 根据测量点id和开始时间,时间间隔来获取所有数据
	 * @param map (cst_id mdi_ts timeInterval)
	 * @return
	 */
	public List<Map<String, Object>> getAllById(Map<String, Object> map);
	
	/**
	 * 根据测量点id获取该测量点最早的时间点
	 * @param csy_id
	 * @return
	 */
	public Long getMinTimeById( @Param("cst_id") String cst_id);
}
