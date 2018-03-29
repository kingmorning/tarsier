/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.service;

import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.engine.RunTask;
import com.tarsier.rule.exception.ExceptionResolver;
import com.tarsier.util.Constant;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Meter;

/**
 * 类LogDispatchService.java的实现描述：Log分发类，操作步骤如下： 1：从消息缓冲queue中获取log信息。
 * 2：根据log中的projectName，获取对应的规则引擎（ruleEngine）。 3：根据log和ruleEngine构造可 执行单元，放入线程池
 * 去做匹配。
 * 
 * @author wangchenchina@hotmail.com 2016年2月11日 上午11:24:56
 */
@Service
public class MsgDispatchService implements Runnable {
	private static final Logger				LOGGER			= LoggerFactory.getLogger(MsgDispatchService.class);
	private int								queueSize		= 10000;
	private final BlockingQueue<MsgEvent>	queue			= new ArrayBlockingQueue<MsgEvent>(queueSize);
	private final Meter						getRequests		= Metrics.newMeter(MsgDispatchService.class,
																	"get-requests", "requests", TimeUnit.SECONDS);
	private Thread							dispathThread	= null;
	private MsgEvent						msg			= null;
	@Autowired
	private EngineHolder					holder;

	public MsgEvent getMsg() {
		return msg;
	}

	/**
	 * 启动告警引擎
	 */
	public void start() {
		LOGGER.info("start...");
		if (dispathThread == null) {
			dispathThread = new Thread(this);
			dispathThread.setName(Constant.DISPATH_THREAD_NAME);
			dispathThread.start();
		}
	}

	/**
	 * 停止告警引擎
	 */
	@PreDestroy
	public void stop() {
		LOGGER.info("stop...");
		int times = 0;
		while (queue.size() > 0 && times++ < 50) {
			try {
				Thread.currentThread().sleep(100);
			}
			catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				MsgEvent msg = queue.take();
				msg = msg;
				getRequests.mark();
				String projectName = msg.getProjectName();
				if (projectName == null) {
					LOGGER.error("can not find projectName from msg:{}", msg.getMessage());
				}
				else {
					Set<Engine> engines = holder.getEngines(projectName);
					if (engines == null || engines.isEmpty()) {
						LOGGER.debug(
								"can not find any rule engines to match logger message, please config rule engines for this projectName:{}.",
								projectName);
					}
					else {
						for (Engine engine : engines) {
							holder.service.submit(new RunTask(msg, engine));
						}
					}
				}
			}
			catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				ExceptionResolver.ErrorMsg = e.getMessage();
			}
		}
	}

	public JSONObject status() {
		JSONObject rate = new JSONObject();
		rate.put("count", getRequests.count());
		rate.put("1_min Rate", getRequests.oneMinuteRate());
		rate.put("5_min Rate", getRequests.fiveMinuteRate());
		rate.put("15_min Rate", getRequests.fifteenMinuteRate());
		rate.put("mean Rate", getRequests.meanRate());
		JSONObject json = new JSONObject();
		json.put("Rate", rate);
		json.put("queue totalSize:", queueSize);
		if (queue != null) {
			json.put("queue size:", queue.size());
			json.put("queue size/total:", (double) queue.size() / queueSize * 100 + "%");
		}
		return json;
	}

	public double rate() {
		return getRequests.oneMinuteRate();
	}

	public BlockingQueue<MsgEvent> getQueue() {
		return queue;
	}
}
