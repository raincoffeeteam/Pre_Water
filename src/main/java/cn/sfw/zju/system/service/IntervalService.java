package cn.sfw.zju.system.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.sfw.zju.system.dao.IntervalDao;

@Service
public class IntervalService {

	private final static Log log = LogFactory.getLog(IntervalService.class);
	
	@Autowired
	private IntervalDao dao;
	
	public List<Map<String, Object>> getAllCstId(){
		return dao.getAllCstId();
	}
	
	public List<Map<String, Object>> getAllById(Map<String, Object> map){
		return dao.getAllById(map);
	}
	
	public Long getMinTimeById(String cst_id){
		return dao.getMinTimeById(cst_id);
	}
}
