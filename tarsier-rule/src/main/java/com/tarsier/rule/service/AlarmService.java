/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarsier.rule.data.AlarmEvent;
import com.tarsier.rule.proxy.AlarmProxy;
import com.tarsier.rule.util.RuleUtil;

/**
 * 类AlarmService.java的实现描述：检查告警信息是否在间隔时间内，将告警事件加入队列，调用其他报警代理发送报警信息。
 * 
 * @author wangchenchina@hotmail.com 2016年2月6日 下午12:39:16
 */
@Service
public class AlarmService implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(AlarmService.class);
	private final Map<String, Long> alarmCheck = new ConcurrentHashMap<>();
	private final BlockingQueue<AlarmEvent> events = new LinkedBlockingQueue<>(100);
	private Thread senderThread = null;
	@Autowired
	private List<AlarmProxy> alarmProxies;

	public boolean send(String key, AlarmEvent alarm) {
		boolean alarmed = false;
		try {
			if (alarm.isAlarm()) {
				Long preTime = alarmCheck.get(key);
				if (preTime == null) {
					alarmCheck.put(key, alarm.getMsg().getSeconds());
					events.add(alarm);
					alarmed = true;
				} else {
					int interval = alarm.getRule().getInterval();
					long curTime = alarm.getMsg().getSeconds();
					if ((curTime - preTime) > interval * 60) {
						alarmCheck.put(key, curTime);
						events.add(alarm);
						alarmed = true;
					} else if (LOG.isDebugEnabled()) {
						LOG.debug("canceled to send alarm. as in interval time[{}] minutes. key: {}", interval, key);
					}
				}
			} else if (alarmCheck.containsKey(key)) {
				alarmCheck.remove(key);
				events.add(alarm);
				alarmed = true;
			}
		} catch (Exception e) {
			LOG.warn("add alarm info to queue fail:{}", alarm);
		}
		return alarmed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				AlarmEvent info = events.take();
				for (AlarmProxy proxy : alarmProxies) {
					if(!proxy.send(info, RuleUtil.content(info))){
						LOG.warn("send alarm fail, proxy:", proxy.getClass().getSimpleName());
					};
				}
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public void stop() {
		LOG.info("stop...");
		events.clear();
		alarmCheck.clear();
	}

	public void start() {
		LOG.info("starting...");
		if (senderThread == null || !senderThread.isAlive()) {
			senderThread = new Thread(this);
			senderThread.setName("alarm service");
			senderThread.start();
		}
	}

}
