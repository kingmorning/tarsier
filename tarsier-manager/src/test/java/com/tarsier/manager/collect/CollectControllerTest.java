package com.tarsier.manager.collect;

import static com.tarsier.util.Constant.ITEM;
import static com.tarsier.util.Constant.SUCCESS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Date;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.data.Collect;
import com.tarsier.manager.data.CollectType;
import com.tarsier.manager.mapper.CollectMapper;
import com.tarsier.manager.service.BeatService;
import com.tarsier.manager.test.AbstractTest;

public class CollectControllerTest extends AbstractTest {

	@Autowired
	private CollectMapper mapper;
	
	private Collect getCollect(int num) {
		Collect ls = new Collect();
		ls.setGroup("group name"+num);
		ls.setName("host name"+num);
		ls.setType(CollectType.filebeat);
		ls.setIp("10.0.0."+num);
		ls.setConfig(BeatService.defaultConfig);
		ls.setUserName(userName);
		ls.setCreateTime(new Date());
		ls.setDisabled(true);
		return ls;
	}
	
	private String expact(Collect ls){
		JSONObject jo = new JSONObject();
		jo.put(SUCCESS, true);
		JSONObject item = new JSONObject();
		item.put("group", ls.getGroup());
		item.put("name", ls.getName());
		item.put("type", ls.getType().name());
		item.put("ip", ls.getIp());
		item.put("config", ls.getConfig());
		item.put("userName", ls.getUserName());
		item.put("disabled", ls.isDisabled());
		jo.put(ITEM, item);
		return JSON.toJSONString(jo);
	}
	
	@Before
	public void setup(){
		mapper.deleteByUser(userName);
	}
	
	@Test
	public void insert() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mockMvc.perform(post("/collect/save").content(JSON.toJSONString(ls)).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expact(ls)));
	}
	@Test
	public void update() throws Exception {
		Collect ls = getCollect(RandomUtils.nextInt(1, 255));
		String ip=ls.getIp();
		mapper.insert(ls);
		int id=ls.getId();
		int num=RandomUtils.nextInt(1, 255);
		ls=getCollect(num);
		ls.setId(id);
		ls.setIp(ip);
		mockMvc.perform(post("/collect/save").content(JSON.toJSONString(ls)).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expact(ls)));
	}
	@Test
	public void delete() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mapper.insert(ls);
		mockMvc.perform(post("/collect/delete").param("id",ls.getId()+"").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expact(ls)));
	}
	@Test
	public void disable() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mapper.insert(ls);
		mockMvc.perform(post("/collect/disable").param("id",ls.getId()+"").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json("{\"success\":true,\"item\":{\"id\":"+ls.getId()+",\"disabled\":true}}"));
	}
	@Test
	public void enable() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mapper.insert(ls);
		ls.setDisabled(false);
		mockMvc.perform(post("/collect/enable").param("id",ls.getId()+"").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expact(ls)));
	}
	@Test
	public void getId() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mapper.insert(ls);
		mockMvc.perform(get("/collect/"+ls.getId()).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expact(ls)));
	}
	@Test
	public void check() throws Exception {
		int num=RandomUtils.nextInt(1, 255);
		Collect ls = getCollect(num);
		mapper.insert(ls);
		mockMvc.perform(post("/collect/check").content(JSON.toJSONString(ls)).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	@Test
	public void list() throws Exception {
		mapper.insert(getCollect(RandomUtils.nextInt(1, 255)));
		mockMvc.perform(get("/collect/list?userName="+userName).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
	
	@Test
	public void group() throws Exception{
		mapper.insert(getCollect(RandomUtils.nextInt(1, 255)));
		mockMvc.perform(get("/collect/group?userName="+userName).accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
		.andExpect(content().json(expactSuccess()));
	}
} 
