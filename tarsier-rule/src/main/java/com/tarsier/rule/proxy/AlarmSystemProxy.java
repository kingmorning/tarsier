/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import com.tarsier.rule.data.AlarmInfo;

/**
 * 类AlarmSystemProxy.java的实现描述：告警系统代理类接口。
 * 
 * @author wangchenchina@hotmail.com 2016年2月6日 下午12:39:16
 */
public interface AlarmSystemProxy {

	/**
	 * @param alarmInfo
	 * @return true:success. false:failed.
	 */
	public boolean alarm(AlarmInfo alarmInfo);

	public void stop();
	public void start();
}
