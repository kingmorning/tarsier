/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.proxy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarsier.rule.api.AbstractTest;
import com.tarsier.rule.data.Engine;
import com.tarsier.rule.parser.Rule2EngineParser;
import com.tarsier.rule.proxy.DBRuleProxy;
import com.tarsier.util.Rule;

/**
 * 类ApiProxyTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月26日 下午3:06:54
 */
public class DBRuleEngineProxyTest extends AbstractTest {

    @Autowired
    private Rule2EngineParser        parser;
    @Autowired
    private DBRuleProxy proxy;

    private Rule create(){
    	Rule rule = new Rule();
    	rule.setName("name"+UUID.randomUUID());
    	rule.setProjectName("projectName"+UUID.randomUUID());
    	rule.setFilter("1=1 and 2=2");
    	rule.setGroup("hostName");
    	rule.setRecover(null);
    	rule.setTimewin(5);
    	rule.setTrigger("count()>1");
    	rule.setDateRange("2015-12-13:2016-01-03,2016-02-05:2016-04-24");
    	rule.setTimeRange(Rule.dailyAll);
    	rule.setWeekly(Rule.weeklyAll);
    	rule.setInterval(5);
    	rule.setPersons("wangchenchina@hotmail.com");
    	rule.setType(1);
    	
    	return rule;
    }
    
    @Test
	public void testCreate() throws Exception {
		proxy.insertRule(create());
	}
    
    @Test
    public void testAll() {
        List<Rule> fs = proxy.listAll();
        List<Engine> es = new ArrayList<Engine>();
        for (Rule f : fs) {
            es.addAll(parser.parse(f));
        }
        assertEquals(fs.size(), es.size());
        LOGGER.info(fs.toString());
    }

}
