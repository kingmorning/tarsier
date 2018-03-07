/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.engine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tarsier.rule.data.Engine;
import com.tarsier.rule.source.HeartBeatSource;
import com.tarsier.util.Constant;

/**
 * 类EngineHolder.java的实现描述：告警引擎 Holder。engineMap中的规则引擎，都是生效状态的。
 * 
 * @author wangchenchina@hotmail.com 2016年2月11日 上午11:12:24
 */
@Service
public class EngineHolder {

	private static final Logger						LOGGER		= LoggerFactory.getLogger(EngineHolder.class);
	@Value("${thread.pool.size}")
	private int										poolSize	= 100;
	// 行解析认为的线程池
	public ExecutorService							service;
	private Set<String>								channels	= new HashSet<>();
	@Autowired
	private HeartBeatSource hbSource;

	// key：项目名称，value：｛key：引擎id，value：规则引擎}
	private final Map<String, Set<Engine>>	engineMap	= new ConcurrentHashMap<String, Set<Engine>>() {

																	public Set<Engine> put(String key, Set<Engine> value) {
																		return super.put(key.toLowerCase(), value);
																	}
																	public Set<Engine> putIfAbsent(String key, Set<Engine> value) {
																		return super.putIfAbsent(key.toLowerCase(), value);
																	}
																	public Set<Engine> get(Object key) {
																		return super.get(key.toString().toLowerCase());
																	}
																};

	/**
	 * @return 所有域
	 */
	public Set<String> getProjectNames() {
		return engineMap.keySet();
	}

	public Set<String> getChannels() {
		return channels;
	}

	/**
	 * 获取指定域中的 所有规则引擎
	 * 
	 * @param key
	 *            域
	 * @return
	 */
	public Set<Engine> getEngines(String key) {
		return engineMap.get(key);
	}

	/**
	 * 根据域 和 规则ID 获取指定 规则引擎
	 * 
	 * @param projectName
	 * @param id
	 * @return
	 */
	public Engine getEngine(String projectName, Integer id) {
		Set<Engine> es = engineMap.get(projectName);
		if(es !=null){
			for(Engine e : es){
				if(e.getId().intValue()==id.intValue()){
					return e;
				}
			}
		}
		return null;
	}

	/**
	 * 注册规则引擎
	 * 
	 * @param engine
	 */
	public void register(Engine engine) {
		LOGGER.info("register engine, engineName:{}, detail:{}, ", engine.getEngineName(), engine.toString());
		String engineName = engine.getEngineName();
		engineMap.putIfAbsent(engineName, new HashSet<>());
		Set<Engine> es = engineMap.get(engineName);
		for(Engine e : es){
			if(e.getId().intValue()==engine.getId().intValue()){
				es.remove(e);
				LOGGER.warn("replace engine:{} by engine:{}", e.getId(), engine);
			}
		}
		es.add(engine);
		hbSource.register(engine);
	}

	/**
	 * 注销规则引擎
	 * 
	 * @param id
	 */
	public void unregister(Integer id) {
		Collection<Set<Engine>> sets = engineMap.values();
		for(Set<Engine> es : sets){
			for(Engine e : es){
				if(e.getId().intValue()==id.intValue()){
					es.remove(e);
					hbSource.unregister(e.getEngineName(), id);
					LOGGER.info("unregister engine engineName:{}, id:{}", e.getEngineName(), id);
				}
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return engineMap.isEmpty();
	}

	public void start() {
		LOGGER.info("start...");
		service = Executors.newFixedThreadPool(poolSize);
		channelsChanged();
	}

	public void stop() {
		LOGGER.info("stop...");
		service.shutdown();
		engineMap.clear();
		channels.clear();
	}

	public boolean channelsChanged() {
		Set<String> updatedChannels = new HashSet<String>();
		for (Set<Engine> es : engineMap.values()) {
			for (Engine e : es) {
				updatedChannels.add(e.getChannel());
			}
		}
		if (updatedChannels.size() != channels.size() || !channels.containsAll(updatedChannels)) {
			LOGGER.info("channels changed. updated to:{}", updatedChannels);
			channels = updatedChannels;
			return true;
		}
		return false;
	}
	
	public Map<String, String> lastTime(){
		Map<String, String> last = new HashMap<>();
		Map<Integer, Long> logMap = new HashMap<>();
		Map<Integer, Long> alarmMap = new HashMap<>();
		for(Set<Engine> es : engineMap.values()){
			for(Engine e : es){
				if(e.getLastLog() !=null){
					long time = e.getLastLog().getTime();
					Long preTime = logMap.get(e.getId());
					if(preTime ==null || preTime.longValue()<time){
						logMap.put(e.getId(), time);
					}
				}
				if(e.getLastAlarm() !=null){
					long time = e.getLastAlarm().getTime();
					Long preTime = alarmMap.get(e.getId());
					if(preTime ==null || preTime.longValue()<time){
						alarmMap.put(e.getId(), time);
					}
				}
			}
		}
		Set<Integer> ids = new HashSet<>(logMap.keySet());
		ids.addAll(alarmMap.keySet());
		for(Integer id : ids){
			Long alamTime=alarmMap.get(id) == null ? 0L : alarmMap.get(id);
			Long logTime=logMap.get(id) == null ? 0L : logMap.get(id);
			last.put(id.toString(), logTime + Constant.COLON + alamTime);
		}
		return last;
	}
}
