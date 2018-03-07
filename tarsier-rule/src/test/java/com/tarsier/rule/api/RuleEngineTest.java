/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.api;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.tarsier.util.Constant;
import com.tarsier.util.Rule;

/**
 * 类HomeApiTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月2日 下午2:31:37
 */
public class RuleEngineTest extends AbstractTest {

	String	projectName	= "log-test";
	Integer	id			= 1;

	@After
	public void stop(){
		app.stop();
	}
	
	public void start() throws Exception {
//		mockMvc.perform(post("/start").accept(jsonUTF8).contentType(MediaType.APPLICATION_JSON)).andDo(print())
//				.andExpect(model().attributeExists(Constant.RESULT)).andExpect(view().name(Constant.TEXT_PAGE));
		app.start();
		Thread.currentThread().sleep(1000);
	}
	
	@Test
	public void test0() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test0");
		rule.setFilter("message !# \"testMessage\"");
		rule.setGroup("host");
		rule.setTimewin(20);
		rule.setTrigger("count () >2");
		rule.setInterval(2);
		rule.setChannel("classpath:test0.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(2, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test1() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test1");
		rule.setFilter(
				"@version>=1 && host=\"localhost6.localprojectName3\" && message # \"3.\"");
		rule.setGroup("host");
		rule.setTrigger("count () = 4 && Sum( @version)>=5 && AVG(responseTime ) > 5");

		rule.setChannel("classpath:test1.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test2() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test2");
		rule.setFilter(
				"@version>=2 && host # \"localhost6\" && responseTime > 3 && type=\"system\"");
		rule.setGroup("host");
		rule.setTimewin(2);
		rule.setTrigger("count () >=100 || (Sum( @version)>=2 && AVG(responseTime ) > 7)");
		rule.setChannel("classpath:test2.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test3() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test3");
		rule.setFilter("host # \"localhost6\" || @version<2 ");
		rule.setGroup("host");
		rule.setTimewin(1);
		rule.setTrigger("count() >=4");

		rule.setChannel("classpath:test3.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test4() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test4");
		rule.setFilter("host # \"localhost6\" && @version>0");
		rule.setGroup("host");
		rule.setTimewin(10);
		rule.setTrigger("mAx (responseTime) >=5 ||  miN(responseTime)=1 && type!=\"system\"");

		rule.setChannel("classpath:test4.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(3, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test5() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test5");
		rule.setFilter("1=1");
		rule.setGroup("host");
		rule.setTimewin(10);
		rule.setTrigger("Max (responseTime) <5 ||  Min(responseTime)=8 && type=\"good\"");

		rule.setChannel("classpath:test5.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(2, holder.getEngine(projectName, id).getAlarmTimes());
	}

	// @Test
	// public void test6() throws Exception {
	// Date date = DateUtil.getDate("2015-03-4 10:01:01", DateUtil.DATE_TIME);
	// EngineForm rule = createForm();
	// rule.setName("test6");
	// rule.setFilter("@timestamp > " + date.getTime());
	// rule.setGroup(null);
	// rule.setTimewin(60 * 24 * 3);
	// rule.setTrigger("count()>=2");
	// proxy.setMockef(rule);
	// rule.setTopic("classpath:test6.txt");
	// start();
	// // Thread.currentThread().sleep(100000);
	// Assert.assertEquals(3, holder.getEngine(projectName,
	// id).getAlarmTimes());
	// }

	@Test
	public void test7() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test5");
		rule.setFilter("message # \"testmessage\" || type=\"GOOD\"");
		rule.setGroup(null);
		rule.setTimewin(10);
		rule.setTrigger("count() =3");

		rule.setChannel("classpath:test7.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	// FOR test interval
	@Test
	public void test8() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test8");
		rule.setFilter("message # \"testMessage\"");
		rule.setGroup(null);
		rule.setTimewin(10);
		rule.setTrigger("count() >1");
		rule.setInterval(10);
		rule.setChannel("classpath:test8.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void test10_1() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test10");
		rule.setFilter(
				"statusCode>=400 && logType=\"access\" && agent !# \"Alibaba.Security.Heimdall\"");
		rule.setGroup(null);
		rule.setTimewin(10);
		rule.setTrigger("count() >=1");
		rule.setInterval(10);
		rule.setChannel("classpath:test10.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}
	@Test
	public void test10_2() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test10");
		rule.setFilter(
				"(logLevel=\"ERROR\" || logLevel=\"FATAL\") && message !# \"ClientAbortException\"");
		rule.setGroup(null);
		rule.setTimewin(10);
		rule.setTrigger("count() >=1");
		rule.setInterval(10);
		rule.setChannel("classpath:test10.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(0, holder.getEngine(projectName, id).getAlarmTimes());
	}
	
	@Test
	public void test11() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("test10");
		rule.setFilter(
				"!@ database || !@ kafka  || database != 1 || kafka != 1 || responseTime > 1000");
		rule.setGroup(null);
		rule.setTimewin(10);
		rule.setTrigger("count() >=3");
		rule.setInterval(10);
		rule.setChannel("classpath:test11.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
	}

	@Test
	public void testRecover() throws Exception {
		Rule rule = defaultRule(projectName);
		rule.setName("testRecover");
		rule.setFilter("message # \"testMessage\"");
		rule.setGroup(null);
		rule.setTimewin(5);
		rule.setTrigger("count() >3");
		rule.setRecover("count() <=2");
		rule.setInterval(30);
		rule.setChannel("classpath:testRecover.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(1, holder.getEngine(projectName, id).getAlarmTimes());
		assertEquals(Constant.GREEN, holder.getEngine(rule.getProjectName(), rule.getId()).getStatus());
	}
	
	@Test
	public void testHDFS() throws Exception {
		projectName="HDFS_Quota";
		Rule rule = defaultRule(projectName);
		rule.setName("testHDFS");
		rule.setFilter("usage_rate > 0.8");
		rule.setGroup("dir");
		rule.setTimewin(1);
		rule.setTrigger("count() >=1");
		rule.setInterval(30);
		rule.setChannel("classpath:hdfsQuota.txt");
		proxy.setMockef(rule);
		start();
		Assert.assertEquals(2, holder.getEngine(projectName, id).getAlarmTimes());
		assertEquals(Constant.RED, holder.getEngine(rule.getProjectName(), rule.getId()).getStatus());
	}
}
