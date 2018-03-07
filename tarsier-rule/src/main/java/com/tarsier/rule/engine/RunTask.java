/*
 * Copyright 2008-2015 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.engine;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.LoadingCache;
import com.tarsier.data.LoggerMsg;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.data.EngineStatus;
import com.tarsier.rule.data.ItemPerform;
import com.tarsier.util.Constant;

/**
 * 类RuleTask.java的实现描述：规则引擎执行单元。每个执行单元 根据获取的消息，和对应的告警引擎，放入线程池 执行。
 * 
 * @author wangchenchina@hotmail.com 2016年8月5日 下午7:21:55
 */
public class RunTask implements Runnable {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(RunTask.class);
	private LoggerMsg			msg;

	private Engine				engine;

	public RunTask(LoggerMsg msg, Engine engine) {
		this.msg = msg;
		this.engine = engine;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" start to parse log:{}", msg.getMessage());
			}
			long start = System.currentTimeMillis();
			boolean shouldAlarm = false;
			boolean actuallyAlarm = false;
			if (engine.filter(msg)) {
				shouldAlarm = engine.trigger(msg);
				if (shouldAlarm) {
					engine.setStatus(Constant.RED);
					actuallyAlarm = engine.sendAlarm(msg);
				}
			}
			if (!shouldAlarm) {
				if(engine.getRecover() ==null){
					engine.setStatus(Constant.GREEN);
				}
				else{
					if(engine.recover(msg)){
						engine.sendRecover(msg);
						engine.setStatus(Constant.GREEN);
					}
					else{
						engine.setStatus(Constant.YELLOW);
					}
				}
			}
			engine.setLastLog(msg);
			if (engine.isDebugModel()) {
				ItemPerform item = new ItemPerform();
				item.setMsgSize(msg.getMessage().length());
				long end = System.currentTimeMillis();
				item.setSpendTime(end - start);
				item.setAlarm(shouldAlarm);
				LoadingCache<String,ItemPerform> items = engine.getData();
				if(items != null){
					items.put(UUID.randomUUID().toString(), item);
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" end to parse log. should to alarm:{}, actually alarm:{}", shouldAlarm, actuallyAlarm);
			}
		}
		catch (Exception efe) {
			LOGGER.error(efe.getMessage(), efe);
		}
	}

}
