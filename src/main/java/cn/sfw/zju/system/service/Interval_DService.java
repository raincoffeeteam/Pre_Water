package cn.sfw.zju.system.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.sfw.zju.system.dao.IntervalDao;
import cn.sfw.zju.system.dao.Interval_DDao;
import cn.sfw.zju.system.vo.Interval_D;

@Service
public class Interval_DService {

	private final static Log log = LogFactory.getLog(Interval_DService.class);
	
	@Autowired
	private Interval_DDao dao;
	
	public List<Map<String, Object>> getByCstIdAndTime(Map<String, Object> map){
		return dao.getByCstIdAndTime(map);
	}

	public Map<String, Object> getMaxDateAndMinDateByCstId(String cst_id){
		return dao.getMaxDateAndMinDateByCstId(cst_id);
	}
	
	public Interval_D getInterval_DByCstIdAndTs(Map<String, Object> map){
		return dao.getInterval_DByCstIdAndTs(map);
	}
	
}
