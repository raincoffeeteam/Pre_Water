/**
 * 
 */
package cn.sfw.zju.system.dao;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.New_Interval_D;

@Repository
public interface New_Interval_DDao {
    public void setNew_Interval_D(New_Interval_D new_Interval_D );
    
    public List<New_Interval_D> getByCstIdAndTime(Map<String, Object> parmMap);
    
    public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id);
    
    public long getMaxTimeByCstId(String cst_id); //通过id获取当前最大时间
    
    public List<New_Interval_D> getAllByCstId(String cst_id); //通过id获取数据
    
    public double getRByTime(long time,String cst_id);//通过时间获取用水量
    
    public New_Interval_D getNew_Interval_DByCstIdAndTs(Map<String, Object> map);
}
