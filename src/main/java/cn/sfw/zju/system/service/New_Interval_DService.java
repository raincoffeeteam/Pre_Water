package cn.sfw.zju.system.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sfw.zju.system.dao.Interval_DDao;
import cn.sfw.zju.system.dao.New_Interval_DDao;
import cn.sfw.zju.system.vo.Interval_D;
import cn.sfw.zju.system.vo.New_Interval_D;

@Service
public class New_Interval_DService {
private final static Log log = LogFactory.getLog(Interval_DService.class);
	
	@Autowired
	private New_Interval_DDao dao;
	
	public void setNew_Interval_D(New_Interval_D new_Interval_D ){
		dao.setNew_Interval_D(new_Interval_D);
	}
	
	public List<New_Interval_D> getByCstIdAndTime(Map<String, Object> parmMap){
		return dao.getByCstIdAndTime(parmMap);
	}
	
	public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id){
		return dao.getMaxDateAndMinDateByCstId(cst_id);
	}
	
	//getMaxTimeByCstId
	public long getMaxTimeByCstId(String cst_id){
		return dao.getMaxTimeByCstId(cst_id);
	}
		
	//getAllByCstId
	public List<New_Interval_D> getAllByCstId(String cst_id){
		return dao.getAllByCstId(cst_id);
	}
	
	public double getRByTime(long time,String cst_id){
		return dao.getRByTime(time,cst_id);
	}
	
	public New_Interval_D getNew_Interval_DByCstIdAndTs(Map<String, Object> map){
		return dao.getNew_Interval_DByCstIdAndTs(map);
	}
	
}
