/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.tarsier.antlr.EventFilter;
import com.tarsier.antlr.EventTrigger;
import com.tarsier.antlr.exception.EventFilterException;
import com.tarsier.antlr.groupby.GroupBy;
import com.tarsier.data.MsgEvent;
import com.tarsier.rule.service.AlarmService;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类RuleEngine.java的实现描述：规则引擎，由规则（Rule）解析而来。是运行态的规则
 * 
 * @author wangchenchina@hotmail.com 2016年2月10日 下午7:43:11
 */
public class Engine {

	private static final Logger LOGGER = LoggerFactory.getLogger(Engine.class);
	@JSONField(serialize = false, deserialize = false)
	private final Rule rule;
	private final String engineName;
	// 过滤器
	private final EventFilter filter;
	// 规则告警触发器
	private final EventTrigger trigger;
	// 规则恢复触发器
	private final EventTrigger recover;
	private MsgEvent latestMsg;
	private AlarmEvent lastAlarm;
	private int alarmTimes;
	// 告警系统proxy
	@JSONField(serialize = false, deserialize = false)
	private final AlarmService alarmService;
	// 规则引擎当前的状态，red：警告状态，yellow：正常状态，green：良好状态（配置了recover
	// 并且达到recover的触发条件，才会有green状态）
	private String status = Constant.GREEN;
	@JSONField(serialize = false, deserialize = false)
	private AlarmEvent alarmEvent;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public JSONObject toJson() {
		JSONObject m = (JSONObject)JSON.toJSON(rule);
		m.put("engineName", engineName);
		m.put("status", status);
		m.put("cache", trigger.getGroupby()==null ? "" : JSON.toJSON(trigger.getGroupby().getCache()));
		m.put("message", latestMsg==null ? "" : latestMsg.getMessage());
		m.put("messageTime", latestMsg==null ? 0 : latestMsg.getTime());
		m.put("alarm", lastAlarm==null ? "" : lastAlarm.getMsg().getMessage());
		m.put("alarmTime", alarmEvent==null ? 0 : alarmEvent.getMsg().getTime());
		return m;
	}

	public Engine(Rule rule, String projectName, EventFilter filterTree, EventTrigger triggerTree, EventTrigger recover,
			AlarmService alarmService) {
		this.rule = rule;
		this.engineName = projectName;
		this.filter = filterTree;
		this.trigger = triggerTree;
		this.recover = recover;
		this.alarmService = alarmService;
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return rule.getId();
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return engineName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return rule.getName();
	}

	public String getChannel() {
		return rule.getChannel();
	}

	public boolean filter(MsgEvent msg) throws EventFilterException {
		return filter.filter(msg.getMappedMsg(), msg.getSeconds());
	}

	public boolean trigger(MsgEvent msg) throws EventFilterException {
		return trigger.trigger(msg.getMappedMsg(), msg.getSeconds());
	}

	public boolean recover(MsgEvent msg) throws EventFilterException {
		return recover != null && recover.trigger(msg.getMappedMsg(), msg.getSeconds());
	}

	/**
	 * 发送恢复通知
	 * 
	 * @param msg
	 */
	public void sendRecover(MsgEvent msg) {
		GroupBy groupby = recover.getGroupby();
		String groupedValue = groupby.getGroupValue(msg.getMappedMsg());
		AlarmEvent ae = new AlarmEvent(rule, msg, engineName, groupedValue);
		ae.setAlarm(false);
		alarmService.send(rule.getId()+":"+engineName+":"+groupedValue, ae);
	}

	/**
	 * 发送告警通知
	 * 
	 * @param msg
	 * @return
	 */
	public boolean sendAlarm(MsgEvent msg) {
		GroupBy groupby = trigger.getGroupby();
		String groupedValue = groupby.getGroupValue(msg.getMappedMsg());
		AlarmEvent ae = new AlarmEvent(rule, msg, engineName, groupedValue);
		boolean alarmed = alarmService.send(rule.getId()+":"+engineName+":"+groupedValue, ae);
		if(alarmed){
			alarmTimes++;
			alarmEvent=ae;
		}
		return alarmed;
	}


	public EventFilter getFilter() {
		return filter;
	}

	public EventTrigger getTrigger() {
		return trigger;
	}

	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public EventTrigger getRecover() {
		return recover;
	}

	public MsgEvent getLastLog() {
		return latestMsg;
	}

	public void setLatestMsg(MsgEvent latestMsg) {
		this.latestMsg = latestMsg;
	}

	public AlarmEvent getLastAlarm() {
		return lastAlarm;
	}

	public void setLastAlarm(AlarmEvent lastAlarm) {
		this.lastAlarm = lastAlarm;
	}

	public int getAlarmTimes() {
		return alarmTimes;
	}

	public Rule getRule() {
		return rule;
	}

	public String getEngineName() {
		return engineName;
	}
}
