package cn.sfw.zju.system.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sfw.zju.system.dao.Interval_HDao;
import cn.sfw.zju.system.vo.Interval_H;
import cn.sfw.zju.system.vo.New_Interval_D;

@Service
public class Interval_HService {
private final static Log log = LogFactory.getLog(Interval_HService.class);
	
	@Autowired
	private Interval_HDao dao;
	
	/**
	 * 根据id 起始日期获取list
	 * @param parmMap
	 * @return
	 */
    public List<Interval_H> getByCstIdAndTime(Map<String, Object> parmMap){
    	return dao.getByCstIdAndTime(parmMap);
    }
	
    /**
     * 根据id获取最大最小日期
     * @param cst_id
     * @return
     */
    public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id){
    	return dao.getMaxDateAndMinDateByCstId(cst_id);
    }
    
  //getMaxTimeByCstId
  	public long getMaxTimeByCstId(String cst_id){
  		return dao.getMaxTimeByCstId(cst_id);
  	}
  		
  	//getAllByCstId
  	public List<Interval_H> getAllByCstId(String cst_id){
  		return dao.getAllByCstId(cst_id);
  	}
  	
  	public double getH0ByTime(long time,String cst_id){
  		return dao.getH0ByTime(time,cst_id);
  	}
  	public Interval_H getAllByCstIdAndTime(Map<String, Object> parmmap){
  		return dao.getAllByCstIdAndTime(parmmap);
  	}
}
