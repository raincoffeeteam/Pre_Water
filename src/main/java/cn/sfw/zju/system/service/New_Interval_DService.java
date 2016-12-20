package cn.sfw.zju.system.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sfw.zju.system.dao.Interval_DDao;
import cn.sfw.zju.system.dao.New_Interval_DDao;
import cn.sfw.zju.system.vo.New_Interval_D;

@Service
public class New_Interval_DService {
private final static Log log = LogFactory.getLog(Interval_DService.class);
	
	@Autowired
	private New_Interval_DDao dao;
	
	public void setNew_Interval_D(New_Interval_D new_Interval_D ){
		dao.setNew_Interval_D(new_Interval_D);
	}
}
