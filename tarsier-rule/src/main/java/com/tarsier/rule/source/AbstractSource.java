/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.data.MsgEvent;
import com.tarsier.rule.exception.ExceptionResolver;

/**
 * 类AbstractSource.java的实现描述：Kafka数据源，从kafka中读取log消息，放入缓冲queue中
 * 
 * @author wangchenchina@hotmail.com 2016年9月22日 下午6:36:05
 */
public abstract class AbstractSource implements Source {

	private static final Logger			LOGGER	= LoggerFactory.getLogger(AbstractSource.class);
	protected final AtomicBoolean		running	= new AtomicBoolean(false);
	private BlockingQueue<MsgEvent>	queue;
	protected String lastMsg;
	public void setQueue(BlockingQueue<MsgEvent> queue) {
		this.queue = queue;
	}

	public void put(MsgEvent msg) {
		try {
			if(msg !=null){
				queue.put(msg);
			}
		}
		catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
			ExceptionResolver.ErrorMsg=e.getMessage()+", msg:"+msg.getMessage();
		}
	}
	
	public void put(String msg) {
		try {
			this.lastMsg=msg;
			this.put(new MsgEvent(msg));
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			ExceptionResolver.ErrorMsg=e.getMessage();
		}
	}
	
	public String getLastMsg() {
		return lastMsg;
	}
}
