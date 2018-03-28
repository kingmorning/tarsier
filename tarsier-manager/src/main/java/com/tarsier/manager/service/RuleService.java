package com.tarsier.manager.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.manager.mapper.RuleMapper;
import com.tarsier.manager.util.UserUtil;
import com.tarsier.util.Constant;
import com.tarsier.util.HttpUtils;
import com.tarsier.util.Rule;

@Service
public class RuleService {
	@Autowired
	private RuleMapper mapper;
	private Map<Integer, RuleStatus> lastTimeMap = new ConcurrentHashMap<>();
	@Value("${default.rule.server}")
	private String defaultServer;
	class RuleStatus{
		long lastLogTime;
		long lastAlartTime;
		String ip;
		int port;
		public RuleStatus(long ltime, long atime, String ip, int port){
			this.lastAlartTime=atime;
			this.lastLogTime=ltime;
			this.ip=ip;
			this.port=port;
		}
		
		public void update(long ltime, long atime, String ip, int port){
			if(ltime > this.lastLogTime){
				this.lastLogTime=ltime;
				if(ip !=null){
					this.ip=ip;
					this.port=port;
				}
			}
			lastAlartTime = atime > lastAlartTime ? atime : lastAlartTime;
		}
	}

	public void updatelastTime(Map<String, String> timeMap, String ip, Integer port) {
		if (timeMap != null) {
			for (String key : timeMap.keySet()) {
				String[] vs = timeMap.get(key).split(Constant.COLON, 2);
				Integer id = Integer.valueOf(key);
				long ltime=Long.valueOf(vs[0]);
				long atime=Long.valueOf(vs[1]);
				if(lastTimeMap.get(id)==null){
					lastTimeMap.put(id, new RuleStatus(ltime, atime, ip, port));
				}
				else{
					lastTimeMap.get(id).update(ltime, atime, ip, port);
				}
			}
		}
	}
	
	public void updatelastTime(Integer id, long ltime, long atime ){
		if(lastTimeMap.containsKey(id)){
			lastTimeMap.get(id).update(ltime, atime, null, 8080);
		}
	}
	
	public String getRuleServer(int id){
		RuleStatus rs = lastTimeMap.get(id);
		if(rs ==null){
			return defaultServer;
		}
		else{
			return "http://"+rs.ip+":"+rs.port+"/tarsier-rule";
		}
	}

	public void save(Rule rule) {
		rule.setChannel("channel");
		rule.setCreateTime(new Date());
		rule.setDateRange("dateRange");
		rule.setFilter("filter");
		rule.setGroup("group");
		rule.setName("name");
		rule.setProjectName("projectName");
		mapper.insert(rule);
	}

	public Rule get(int id) {
		Rule rule = mapper.selectById(id);
		return rule;
	}

	public List<Rule> list(String userName, String disabled) {
		Map<String, Object> param = new HashMap<>();
		if(!UserUtil.isAdmin(userName)){
			param.put("userName", userName);
		}
		if (disabled != null) {
			param.put("disabled", Boolean.valueOf(disabled) ? "1" : "0");
		}
		List<Rule> rs = mapper.select(param);
		return rs;
	}

	public List<Rule> listByIp(String ip, String disabled) {
		Map<String, Object> param = new HashMap<>();
		// TODO
		// param.put("ip", ip);
		if (disabled != null) {
			param.put("disabled", Boolean.valueOf(disabled) ? "1" : "0");
		}
		return mapper.select(param);
	}

	public void insert(Rule rule) {
		preCheck(rule);
		rule.setCreateTime(new Date());
		mapper.insert(rule);
		if(!rule.isDisabled()){
			syncRuleServer(rule.getId());
		}
	}

	private void preCheck(Rule rule) {
		if (StringUtils.isEmpty(rule.getTimeRange())) {
			rule.setTimeRange("0:24");
		}
		if (StringUtils.isEmpty(rule.getDateRange()) && StringUtils.isEmpty(rule.getMonthly())
				&& StringUtils.isEmpty(rule.getWeekly())) {
			rule.setWeekly("0,1,2,3,4,5,6");
		}
		if(StringUtils.isEmpty(rule.getEmails()) && StringUtils.isNotEmpty(rule.getPersons())){
			String emails = rule.getPersons().replaceAll(Constant.COMMA, Constant.EMAIL_SUFFIX+Constant.COMMA);
			if(emails.length()>0&&!emails.endsWith(Constant.EMAIL_SUFFIX)){
				emails = emails+Constant.EMAIL_SUFFIX;
			}
			rule.setEmails(emails);
		}
	}

	public void update(Rule rule) {
		preCheck(rule);
		mapper.update(rule);
		syncRuleServer(rule.getId());
	}

	public void delete(Integer id) {
		mapper.delete(id);
		syncRuleServer(id);
	}

	public void status(Integer id, boolean s) {
		mapper.disable(id, s);
		syncRuleServer(id);
	}

	public List<Map<String, String>> persons(String name, String mobile) {
		return mapper.persons(name == null ? "" : name, mobile == null ? "" : mobile);
	}

	public List<String> projects(String userName) {
		return mapper.projects(userName);
	}

	public JSONObject buildLastTime(Rule rule) {
		JSONObject r = (JSONObject) JSON.toJSON(rule);
		if(r == null){
			return null;
		}
		else{
			RuleStatus rs = lastTimeMap.get(rule.getId());
			r.put("lastLogTime", rs != null ? rs.lastLogTime : null);
			r.put("lastAlarmTime", rs != null ? rs.lastAlartTime : null);
			return r;
		}
	}
	
	private void syncRuleServer(final int id){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				HttpUtils.postJson(HttpClients.createDefault(), getRuleServer(id) + "/rule/sync", new JSONObject());
			}
		}).start();
	}
	
	public long getLastLogTime(){
		long logTime=0;
		for(RuleStatus value : lastTimeMap.values()){
			if(logTime < value.lastLogTime){
				logTime = value.lastLogTime;
			}
		}
		return logTime;
	}
}
