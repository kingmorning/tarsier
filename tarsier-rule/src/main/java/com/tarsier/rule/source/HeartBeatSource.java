package com.tarsier.rule.source;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.Engine;
import com.tarsier.util.DateUtil;


public class HeartBeatSource extends AbstractSource {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatSource.class);
	private Timer				timer;
	private TimerTask			timerTask;
	private final Map<String, Set<Integer>> heartMap=new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		timer = new Timer("HeartBeatSource", true);
		timerTask = new TimerTask() {
			
			@Override
			public void run() {
				try{
					long time = System.currentTimeMillis();
					for(String projectName : heartMap.keySet()){
						put(new MsgEvent(projectName, time));
						lastMsg=projectName+", "+DateUtil.getDateByTime(time+"", DateUtil.DATE_TIME_SLASH);
					}
				}
				catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			
		};
		timer.schedule(timerTask, 30*1000, 1000);
	}

	@Override
	public void start(Set<String> channels){
		
	}

	@Override
	public void stop() {
	}

	@Override
	public void restart(Set<String> channels) {
	}
	
	public void register(Engine engine){
		String expr = engine.getTrigger().getExpression();
		String en = engine.getEngineName();
		if(expr.contains("<")){
			if(!heartMap.containsKey(en)){
				heartMap.put(en, new HashSet<Integer>());
			}
			heartMap.get(en).add(engine.getId());
		}
	}
	
	public void unregister(String engineName, Integer id){
		if(heartMap.containsKey(engineName)){
			Set<Integer> ids = heartMap.get(engineName);
			ids.remove(id);
			if(ids.isEmpty()){
				heartMap.remove(engineName);
			}
		}
	}

	@Override
	public JSONObject getConfig() {
		return (JSONObject)JSON.toJSON(heartMap);
	}

}
