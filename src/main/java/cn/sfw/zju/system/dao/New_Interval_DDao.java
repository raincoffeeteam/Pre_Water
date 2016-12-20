/**
 * 
 */
package cn.sfw.zju.system.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.sfw.zju.system.vo.New_Interval_D;

@Repository
public interface New_Interval_DDao {
    public void setNew_Interval_D(New_Interval_D new_Interval_D );
    //@Param("new_Interval_D")
}
