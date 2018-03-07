package com.tarsier.manager.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.tarsier.manager.mapper.RuleMapper;
import com.tarsier.util.Rule;

public class RuleControllerTest extends AbstractTest {
	
	@Autowired
	private RuleMapper mapper;
	
	private Rule generate(){
		Rule rule =new Rule();
		rule.setChannel("testChannel");
		rule.setPersons("wangchenchina@hotmail.com");
		rule.setFilter("1=1");
		rule.setInterval(20);
		rule.setMobiles("13774307090");
		rule.setName("testName");
		rule.setProjectName("projectName");
		rule.setTimewin(10);
		rule.setTrigger("count()>1");
		rule.setType(3);
		rule.setUserName(userName);
		rule.setCreateTime(new Date());
		rule.setTimeRange("0:24");
		return rule;
	}
	
	@Test
	public void insert() throws Exception {
		Rule rule = generate();
		mockMvc.perform(post("/rule/insert").content(JSON.toJSONString(rule)).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
				.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void update() throws Exception{
		Rule r = generate();
		mapper.insert(r);
		r.setName("updatedName");
		mockMvc.perform(post("/rule/update").content(JSON.toJSONString(r)).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void disable() throws Exception{
		Rule r = generate();
		mapper.insert(r);
		mockMvc.perform(post("/rule/disable?id="+r.getId()).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void delete() throws Exception{
		Rule r = generate();
		mapper.insert(r);
		mockMvc.perform(post("/rule/delete?id="+r.getId()).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void getId() throws Exception {
		Rule r = generate();
		mapper.insert(r);
		mockMvc.perform(get("/rule/"+r.getId()).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void list() throws Exception {
		mockMvc.perform(get("/rule/list?userName="+userName).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void allPersons() throws Exception {
		mockMvc.perform(get("/rule/persons").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	@Test
	public void persons() throws Exception {
		mockMvc.perform(get("/rule/persons?wanxin=wangchen").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
}
