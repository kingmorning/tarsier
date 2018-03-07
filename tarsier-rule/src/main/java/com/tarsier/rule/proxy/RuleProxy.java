/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import java.util.List;

import com.tarsier.util.Rule;

/**
 * 类RuleEngineProxyI.java的实现描述：规则 系统代理类，用来获取规则列表及监听的topic信息
 * 
 * @author wangchenchina@hotmail.com 2016年3月2日 下午5:29:02
 */
public interface RuleProxy {

	/**
	 * 获取所有启用规则
	 * 
	 * @return
	 */
	public List<Rule> listEnabled();
	/**
	 * 获取所有规则
	 * 
	 * @return
	 */
	public List<Rule> listAll();

	/**
	 * 重置所有 规则及topic
	 */
	void reset();

	public void insertRule(Rule rule);
	public void updateRule(Rule rule);
	public void deleteRule(int id);
	public void updateStatus(int id, boolean status);
}
