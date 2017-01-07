package cn.sfw.zju.system.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.sfw.zju.system.dao.Interval_HDao;
import cn.sfw.zju.system.vo.Interval_H;

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

}
