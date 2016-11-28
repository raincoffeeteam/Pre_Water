package cn.sfw.zju.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IntervalDao {
	/**
	 * ��ȡ���й۲���id��name
	 * @return
	 */
	public List<Map<String, Object>> getAllCstId();
	
	/**
	 * ���ݲ�����id�Ϳ�ʼʱ��,ʱ��������ȡ��������
	 * @param map (cst_id mdi_ts timeInterval)
	 * @return
	 */
	public List<Map<String, Object>> getAllById(Map<String, Object> map);
	
	/**
	 * ���ݲ�����id��ȡ�ò����������ʱ���
	 * @param csy_id
	 * @return
	 */
	public Long getMinTimeById( @Param("cst_id") String cst_id);
}
