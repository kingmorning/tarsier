/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.data;

import java.util.HashMap;
import java.util.Map;

import com.tarsier.util.Constant;
import com.tarsier.util.DateUtil;

/**
 * 类LoggerMsg.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 下午1:54:37
 */
public class LoggerMsg {

	private final Map<String, String> mappedMsg;
	private final String message;
	private long time;
	private final String projectName;

	public LoggerMsg(String msg) {
		this.message = msg;
		this.mappedMsg = LogParser.parse(msg);
		this.projectName = mappedMsg.get(Constant.PROJECT_NAME);
		formatTime();
	}

	public LoggerMsg(String prjectName, long time) {
		this.projectName = prjectName;
		this.time = time;
		mappedMsg = new HashMap<String, String>();
		message = null;
	}

	private void formatTime() {
		String timestamp = mappedMsg.get(Constant.TIME_STAMP);
		try {
			if (timestamp == null) {
				time = DateUtil.DATE_TIME_TZ.parse(mappedMsg.get(Constant.UTC_TIME_STAMP)).getTime();
			} else {
				time = DateUtil.DATE_TIME_T.parse(timestamp).getTime();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("can not parse timestamp from msg:" + message);
		}
	}

	/**
	 * @return the projectName
	 */
	public String getProjectName() {
		return projectName;
	}

	// /**
	// * @return the minutes
	// */
	// public long getMinutes() {
	// return time / 1000 / 60;
	// }
	//
	/**
	 * @return the minutes
	 */
	public long getSeconds() {
		return time / 1000;
	}

	/**
	 * @return the minutes
	 */
	public long getTime() {
		return time;
	}

	public String getMessage() {
		return message;
	}

	public Map<String, String> getMappedMsg() {
		return mappedMsg;
	}
}
