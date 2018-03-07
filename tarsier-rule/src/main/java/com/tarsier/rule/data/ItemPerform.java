/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.data;

/**
 * 类ItemPerform.java的实现描述：执行状况跟踪类。用来记录每个消息处理的处理过程，包括消息大小，使用时间，及最后是否触发告警。
 * 
 * @author wangchenchina@hotmail.com 2016年1月31日 下午5:04:24
 */
@Deprecated
public class ItemPerform {

	private int		spendTime;
	private int		msgSize;
	private boolean	alarm	= false;

	/**
	 * @return the spendTime
	 */
	public int getSpendTime() {
		return spendTime;
	}

	/**
	 * @param spendTime
	 *            the spendTime to set
	 */
	public void setSpendTime(long spendTime) {
		this.spendTime = (int) spendTime;
	}

	/**
	 * @return the msgSize
	 */
	public int getMsgSize() {
		return msgSize;
	}

	/**
	 * @param msgSize
	 *            the msgSize to set
	 */
	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}

	/**
	 * @return the alarm
	 */
	public boolean isAlarm() {
		return alarm;
	}

	/**
	 * @param alarm
	 *            the alarm to set
	 */
	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
}
