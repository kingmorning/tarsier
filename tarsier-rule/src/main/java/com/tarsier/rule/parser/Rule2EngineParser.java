/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tarsier.antlr.EventFilter;
import com.tarsier.antlr.EventTrigger;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.proxy.AlarmSystemProxy;
import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类Rule2EngineParser.java的实现描述：规则引擎解析类。根据规则（Rule）解析为可执行的 规则引擎（Engine）
 * 
 * @author wangchenchina@hotmail.com 2016年2月11日 上午11:22:34
 */
@Service
public class Rule2EngineParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(Rule2EngineParser.class);
	@Autowired
	private AlarmSystemProxy alarmService;

	public List<Engine> parse(Rule ef) {
		LOGGER.info("start to parse rule :{}", ef);
		List<Engine> res = new ArrayList<Engine>();
		String[] projectNames = ef.getProjectName().split(Constant.COMMA);
		for (String projectName : projectNames) {
			if (StringUtils.isNotBlank(projectName)) {
				EventFilter filter = new EventFilter(ef.getFilter());
				EventTrigger trigger = new EventTrigger(ef.getTrigger(), ef.getGroup(), ef.getTimewin() * 60);
				EventTrigger recover = StringUtils.isBlank(ef.getRecover()) ? null : new EventTrigger(ef.getRecover(), ef.getGroup(), ef.getTimewin() * 60);
				Engine e = new Engine(ef, projectName, filter, trigger, recover, alarmService);
				res.add(e);
				LOGGER.info("parse rule to engine success :{}", e.toString());
			}
		}
		return res;
	}
}
