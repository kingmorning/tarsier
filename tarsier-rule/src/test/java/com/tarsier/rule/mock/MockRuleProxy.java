/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.mock;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.proxy.BaseRuleProxy;
import com.tarsier.util.Rule;

/**
 * 绫籑ockRuleEngineProxy.java鐨勫疄鐜版弿杩帮細TODO 绫诲疄鐜版弿杩� *
 * 
 * @author wangchenchina@hotmail.com 2016骞�鏈�鏃�涓嬪崍5:07:43
 */
public class MockRuleProxy extends BaseRuleProxy {

	private static final Logger	LOGGER		= LoggerFactory.getLogger(MockRuleProxy.class);
	@Autowired
	private EngineHolder		holder;
	private boolean				called		= false;
	private Rule				mockef		= null;
	private List<Rule>			mockLists	= null;

	public void setMockef(Rule ef) {
		this.mockef = ef;
		called = false;
	}

	private Rule createForm() {
		if (mockef != null) {
			return mockef;
		}
		Rule ef = new Rule();
		ef.setId(1);
		ef.setProjectName("log-test");
		ef.setName("test rule engine");
		ef.setTimeRange(Rule.dailyAll);
		ef.setWeekly(Rule.weeklyAll);
		ef.setGroup("@version");
		ef.setTimewin(5);
		ef.setFilter("projectName=\"log-test\"");
		ef.setTrigger("count(@version) > 4");
		return ef;
	}

	@Override
	public List<Rule> listEnabled() {
		if (mockLists != null) {
			return mockLists;
		}
		else if (called) {
			return null;
		}
		else {
			called = true;
			List<Rule> forms = new ArrayList<Rule>();
			forms.add(createForm());
			return forms;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tarsier.rule.proxy.RuleEngineProxy#reset()
	 */
	@Override
	public void reset() {
		called = false;
	}

	public List<Rule> getMockLists() {
		return mockLists;
	}

	public void setMockLists(List<Rule> mockLists) {
		this.mockLists = mockLists;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tarsier.rule.proxy.RuleEngineProxy#listAll()
	 */
	@Override
	public List<Rule> listAll() {
		return listEnabled();
	}

}
