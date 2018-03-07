/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.data;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tarsier.antlr.EventFilter;
import com.tarsier.antlr.EventTrigger;
import com.tarsier.antlr.exception.EventFilterException;
import com.tarsier.antlr.groupby.GroupBy;
import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.proxy.AlarmSystemProxy;
import com.tarsier.rule.util.RuleUtil;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类RuleEngine.java的实现描述：规则引擎类，由规则（ruleForm）解析而来。是解析告警的主要逻辑类。
 * 
 * @author wangchenchina@hotmail.com 2016年2月10日 下午7:43:11
 */
public class Engine {

	private static final Logger LOGGER = LoggerFactory.getLogger(Engine.class);
	@JSONField(serialize = false, deserialize = false)
	private final Rule rule;
	private final String engineName;
	// 缓存告警信息，根据设置的告警间隔时间 自动过期。防止在间隔时间内，重复告警
	@JSONField(serialize = false, deserialize = false)
	private final LoadingCache<String, AlarmInfo> alarmCache;
	// 过滤器
	private final EventFilter filter;
	// 规则告警触发器
	private final EventTrigger trigger;
	// 规则恢复触发器
	private final EventTrigger recover;
	private LoggerMsg lastLog;
	private AlarmInfo lastAlarm;
	private int alarmTimes;
	// 告警系统proxy
	@JSONField(serialize = false, deserialize = false)
	private final AlarmSystemProxy alarmService;
	// 规则引擎当前的状态，red：警告状态，yellow：正常状态，green：良好状态（配置了recover
	// 并且达到recover的触发条件，才会有green状态）
	private String status = Constant.GREEN;
	private boolean debugModel = false;
	@JSONField(serialize = false, deserialize = false)
	private LoadingCache<String, ItemPerform> data = null;

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
		m.put("lastLog", lastLog==null ? "" : lastLog.getMessage());
		m.put("lastLogTime", lastLog==null ? 0 : lastLog.getTime());
		m.put("lastAlarm", lastAlarm==null ? "" : lastAlarm.getMessage());
		m.put("lastAlarmTime", lastAlarm==null ? 0 : lastAlarm.getTime());
		return m;
	}

	public Engine(Rule rule, String projectName, EventFilter filterTree, EventTrigger triggerTree, EventTrigger recover,
			AlarmSystemProxy alarmService) {
		this.rule = rule;
		this.engineName = projectName;
		this.filter = filterTree;
		this.trigger = triggerTree;
		this.recover = recover;
		this.alarmService = alarmService;

		this.alarmCache = CacheBuilder.newBuilder()
				.expireAfterWrite(rule.getInterval() <= 0 ? 30 : rule.getInterval(), TimeUnit.MINUTES)
				.build(new CacheLoader<String, AlarmInfo>() {

					public AlarmInfo load(String key) {
						return new AlarmInfo(-1);
					}
				});
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

	public synchronized boolean filter(LoggerMsg msg) throws EventFilterException {
		return filter.filter(msg.getMappedMsg(), msg.getSeconds());
	}

	public synchronized boolean trigger(LoggerMsg msg) throws EventFilterException {
		return trigger.trigger(msg.getMappedMsg(), msg.getSeconds());
	}

	public synchronized boolean recover(LoggerMsg msg) throws EventFilterException {
		return recover != null && recover.trigger(msg.getMappedMsg(), msg.getSeconds());
	}

	/**
	 * 发送警告接触通知
	 * 
	 * @param msg
	 */
	public synchronized void sendRecover(LoggerMsg msg) {
		GroupBy groupby = recover.getGroupby();
		String groupValue = groupby.getGroupValue(msg.getMappedMsg());
		if (isAlarmed(groupValue)) {
			lastAlarm = RuleUtil.recoverContent(this, recover.getFunctionMap(), msg);
			alarmService.alarm(lastAlarm);
			alarmCache.invalidate(groupValue);
		}
	}

	/**
	 * 发送告警信息
	 * 
	 * @param msg
	 * @return
	 */
	public synchronized boolean sendAlarm(LoggerMsg msg) {
		GroupBy groupby = trigger.getGroupby();
		String groupValue = groupby.getGroupValue(msg.getMappedMsg());
		if (!isAlarmed(groupValue)) {
			lastAlarm = RuleUtil.alarmContent(this, groupValue, trigger.getFunctionMap(), msg);
			alarmService.alarm(lastAlarm);
			alarmCache.put(groupValue, lastAlarm);
			LOGGER.info("put ai, type:{}, gv:{}", lastAlarm.getType(), groupValue);
			alarmTimes++;
			return true;
		} else if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("canceled to send alarm. as in interval time[{}] minutes. ruleId: {}", rule.getInterval(),
					rule.getId());
		}
		return false;
	}

	/**
	 * 根据groupValue，判断是否处于告警间隔范围内。 同一个规则的 相同分组值，不能在规定时间内重复发送告警信息。
	 * 
	 * @param groupValue
	 * @return
	 */
	private boolean isAlarmed(String groupValue) {
		try {
			AlarmInfo ai = alarmCache.get(groupValue);
			return ai.getType() >= 0;
		} catch (ExecutionException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}

	// /**
	// * @return the perform
	// */
	// public EngineStatus getEngineStatus() {
	// return engineStatus;
	// }

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

	// public String getChannel() {
	// return channel;
	// }
	//
	// public void setChannel(String channel) {
	// this.channel = channel;
	// }

	public EventTrigger getRecover() {
		return recover;
	}

	public LoggerMsg getLastLog() {
		return lastLog;
	}

	public void setLastLog(LoggerMsg lastLog) {
		this.lastLog = lastLog;
	}

	public AlarmInfo getLastAlarm() {
		return lastAlarm;
	}

	public void setLastAlarm(AlarmInfo lastAlarm) {
		this.lastAlarm = lastAlarm;
	}

	public boolean isDebugModel() {
		return debugModel;
	}

	public void setDebugModel(boolean debugModel) {
		this.debugModel = debugModel;
		if (debugModel == true) {
			if (data == null) {
				data = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<String, ItemPerform>() {

					public ItemPerform load(String key) throws Exception {
						return null;
					}
				});
			}
		} else if (getData() != null) {
			data.invalidateAll();
			setData(null);
		}
	}

	public LoadingCache<String, ItemPerform> getData() {
		return data;
	}

	public void setData(LoadingCache<String, ItemPerform> data) {
		this.data = data;
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
