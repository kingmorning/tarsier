/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.alibaba.fastjson.JSONObject;
import com.tarsier.data.MsgEvent;

/**
 * 类Source.java的实现描述：数据源接口，获取数据的地方
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 上午11:24:40
 */
public interface Source {
	/**
	 * 开始
	 */
	void start(Set<String> channels);

	/**
	 * 停止
	 */
	void stop();

	void restart(Set<String> channels);
	void setQueue(BlockingQueue<MsgEvent> queue);
	/**
	 * 获取配置信息
	 * 
	 * @return
	 */
	JSONObject getConfig();
	
	String getLastMsg();

}
