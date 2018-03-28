package com.tarsier.manager.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.tarsier.util.Rule;

public interface RuleMapper {

	void insert(Rule rule);
	
	List<Rule> select(Map<String, Object> param);
	
	void update(Rule rule);
	
	@Select("SELECT * FROM `rule` WHERE id = #{id}")
	Rule selectById(int id);
	@Delete("delete from `rule` where id=#{id}")
	void delete(Integer id);
	@Update("update `rule` set `disabled`=#{disabled} where id=#{id}")
	void disable(@Param("id")int id, @Param("disabled") boolean b);

	@Select("select name, mobile, email from `persons` where name like  concat('%',#{name},'%') and mobile like  concat('%',#{mobile},'%')")
	List<Map<String, String>> persons(@Param("name")String name, @Param("mobile")String mobile);

	@Select("SELECT distinct project_name projectName FROM `rule` WHERE user_name=#{userName}")
	List<String> projects(String userName);

//	@Select("select name from `persons`")
//	List<String> allPersons();
} 
