package cn.sfw.zju.system.dao;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import cn.sfw.zju.system.vo.Interval_H;

@Repository
public interface Interval_HDao {
   
	/**
	 * 根据id 起始日期获取list
	 * @param parmMap
	 * @return
	 */
    public List<Interval_H> getByCstIdAndTime(Map<String, Object> parmMap);
	
    /**
     * 根据id获取最大最小日期
     * @param cst_id
     * @return
     */
    public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id);
    
    
    
}
