package com.tarsier.manager.controller;

import static com.tarsier.util.Constant.ITEM;
import static com.tarsier.util.Constant.ITEMS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.service.RuleService;
import com.tarsier.util.Constant;
import com.tarsier.util.HttpUtils;
import com.tarsier.util.Rule;

@Controller
@RequestMapping("/rule")
public class RuleController {
	private static final Logger LOG = LoggerFactory.getLogger(RuleController.class);
	@Autowired
	private RuleService ruleService;

	/**
	 * 查看数据库指定 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object id(@PathVariable("id") Integer id) {
		return jsonObj(id);
	}

	/**
	 * 查看规则详情信息
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ResponseBody
	public Object detail(@RequestParam("id") Integer id) throws IOException {
		Rule rule = ruleService.get(id);
//		String ip = rule.getIp();
		String resp = HttpUtils.get(HttpClients.createDefault(), ruleService.getRuleServer(id) + "/engines/" + id);
		JSONObject js = JSON.parseObject(resp);
		JSONObject result = js.getJSONObject("result");
		
		if(result !=null){
			ruleService.updatelastTime(id, result.getLongValue("lastLogTime"), result.getLongValue("lastAlarmTime"));
		}
		else{
			result = ruleService.buildLastTime(rule);
			result.put("engineName", null);
			result.put("status", null);
			result.put("cache", null);
			result.put("lastLog", null);
			result.put("lastAlarm", null);
		}
		
		JSONObject jo = new JSONObject();
		jo.put(ITEM, result);
		jo.put(Constant.SUCCESS, true);
		
		return jo;
	}

	/**
	 * 查看数据库所有 告警规则
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ResponseBody
	public Object list(@RequestParam("userName") String userName, @RequestParam(value = "disabled", required = false) String disabled) {
		List<Rule> rules = ruleService.list(userName, disabled);
		List<JSONObject> rs = new ArrayList<>();
		for(Rule r : rules){
			rs.add(ruleService.buildLastTime(r));
		}
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, rs);
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

	/**
	 * 新增
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	@ResponseBody
	public Object insert(@RequestBody Rule rule) {
		ruleService.insert(rule);
		
		LOG.info("insert rule:{}", JSON.toJSONString(rule));
		return jsonObj(rule.getId());
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ResponseBody
	public Object update(@RequestBody Rule rule) {
		ruleService.update(rule);
		LOG.info("update rule:{}", JSON.toJSONString(rule));
		return jsonObj(rule.getId());
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public Object delete(@RequestParam("id") Integer id) {
		JSONObject jo = jsonObj(id);
		ruleService.delete(id);
		LOG.info("delete ruleId:{}", id);
		return jo;
	}

	@RequestMapping(value = "/enable", method = RequestMethod.POST)
	@ResponseBody
	public Object enable(@RequestParam("id") Integer id) {
		ruleService.status(id, false);
		LOG.info("enable ruleId:{}", id);
		return jsonObj(id);
	}

	@RequestMapping(value = "/disable", method = RequestMethod.POST)
	@ResponseBody
	public Object disable(@RequestParam("id") Integer id) {
		ruleService.status(id, true);
		LOG.info("disable ruleId:{}", id);
		return jsonObj(id);
	}

	@RequestMapping(value = "/persons", method = RequestMethod.GET)
	@ResponseBody
	public Object persons(@RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "mobile", required = false) String mobile) {
		List<Map<String, String>> ps = ruleService.persons(name, mobile);
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, ps);
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	@ResponseBody
	public Object projects(@RequestParam("userName") String userName) {
		List<String> ps = ruleService.projects(userName);
		JSONObject jo = new JSONObject();
		jo.put(ITEMS, ps);
		jo.put(Constant.SUCCESS, true);
		return jo;
	}

	private JSONObject jsonObj(int id) {
		JSONObject jo = new JSONObject();
		jo.put(ITEM, ruleService.buildLastTime(ruleService.get(id)));
		jo.put(Constant.SUCCESS, true);
		return jo;
	}
	
}
