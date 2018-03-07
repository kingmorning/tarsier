package com.tarsier.manager.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.tarsier.manager.data.Collect;

public interface CollectMapper {
	@Select("select * from collect where id=#{id}")
	Collect selectById(int id);

	List<Collect> select(Map<String, String> map);
	
	@Select("select * from collect where disabled=0 and type='logstash'")
	List<Collect> enabledLogstash();

	int insert(Collect ls);

	int update(Collect ls);

	@Select("SELECT * FROM collect WHERE ip = #{ip}")
	List<Collect> selectByIp(String ip);
	
	@Delete("delete from collect where id=#{id}")
	void delete(int id);
	
	@Delete("delete from collect where user_name=#{userName}")
	void deleteByUser(String userName);

	@Update("update collect set `disabled`=#{disabled} where id=#{id}")
	void disable(@Param("id")int id, @Param("disabled") boolean b);

	@Select("select distinct `group` from collect where user_name=#{userName}")
	List<String> group(String userName);
	
	@Select("select distinct `group` from collect")
	List<String> allGroup();
	
	@Update("update collect set `config`=#{config} where id=#{id}")
	void updateConfig(@Param("id")int id, @Param("config")String config);
}
