/*
 * Copyright 2008-2015 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.Engine;
import com.tarsier.util.Constant;

/**
 * 类RuleTask.java的实现描述：规则引擎执行单元。每个执行单元 根据获取的消息，和对应的告警引擎，放入线程池 执行。
 * 
 * @author wangchenchina@hotmail.com 2016年8月5日 下午7:21:55
 */
public class RunTask implements Runnable {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(RunTask.class);
	private MsgEvent			msg;

	private Engine				engine;

	public RunTask(MsgEvent msg, Engine engine) {
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
			boolean trigger = false;
			boolean alarmed = false;
			if (engine.filter(msg)) {
				trigger = engine.trigger(msg);
				if (trigger) {
					engine.setStatus(Constant.RED);
					alarmed = engine.sendAlarm(msg);
				}
			}
			if (!trigger) {
				if(engine.getRecover() ==null){
					engine.setStatus(Constant.GREEN);
				}
				else{
					if(Constant.RED.equalsIgnoreCase(engine.getStatus()) && engine.recover(msg)){
						engine.setStatus(Constant.GREEN);
						engine.sendRecover(msg);
					}
					else{
						engine.setStatus(Constant.YELLOW);
					}
				}
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" end to parse log. should to alarm:{}, actually alarm:{}", trigger, alarmed);
			}
		}
		catch (Exception efe) {
			LOGGER.error(efe.getMessage(), efe);
		}
	}

}
