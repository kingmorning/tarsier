/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.api;

import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.mock.MockRuleProxy;
import com.tarsier.rule.service.Application;
import com.tarsier.util.Rule;

/**
 * 类AbstractTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年1月30日 上午11:26:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextHierarchy({@ContextConfiguration(name = "parent", locations = {"classpath:applicationContext_test.xml"})})
public abstract class AbstractTest {

	protected static final Logger	LOGGER		= LoggerFactory.getLogger(AbstractTest.class);

	protected static MediaType		jsonUTF8	= new MediaType("application", "json", Charset.forName("UTF-8"));

	@Autowired
	protected WebApplicationContext	wac;

	protected MockMvc				mockMvc;

	@Autowired
	protected EngineHolder			holder;
	@Autowired
	protected Application			app;
	@Autowired
	protected MockRuleProxy			proxy;

	@BeforeClass
	public static void beforeClass() {

	}

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	protected Rule defaultRule(String projectName) {
		Rule r = new Rule();
		r.setId(1);
		r.setProjectName(projectName);
		r.setName("test rule engine");
		r.setWeekly(Rule.weeklyAll);
		r.setTimeRange(Rule.dailyAll);
		r.setTimewin(1);
		r.setInterval(30);
		r.setPersons("wangchen");
		r.setEmails("wangchenchina@hotmail.com");
		r.setType(7);
		return r;
	}
	

}
