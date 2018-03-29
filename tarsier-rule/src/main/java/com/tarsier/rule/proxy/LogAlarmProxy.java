/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.rule.data.AlarmEvent;

/**
 * 类LogAlarmProxy.java的实现描述：将报价信息输出到日志中，主要用于测试。
 * 
 * @author wangchenchina@hotmail.com 2018年2月6日 下午12:39:16
 */
public class LogAlarmProxy implements AlarmProxy {

	private Logger LOG = LoggerFactory.getLogger(LogAlarmProxy.class);

	public boolean send(AlarmEvent ai, String content) {
		try {
			LOG.error(content);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

}
