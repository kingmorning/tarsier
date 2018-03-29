/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.antlr;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.antlr.EventFilter;
import com.tarsier.antlr.EventTrigger;
import com.tarsier.antlr.exception.EventFilterException;
import com.tarsier.data.MsgEvent;

/**
 * 类AntlrTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年2月15日 下午2:36:14
 */
public class AntlrRuleTest {

    private static final Logger LOGGER    = LoggerFactory.getLogger(AntlrRuleTest.class);

    private final int           pastTime  = 60*60;

    private String              testMsg   = "{\"message\":\"2018-02-01 08:00:00,014 WARN  [MobileUserServiceExtImpl] -  get user id error with 0b46523f3f13a6b4d3e565c22a6472bb having NO_DATA\",\"@version\":\"1\",\"timestamp\":\"2019-02-01T00:01:01 \",\"type\":\"log4j\",\"projectName\":\"testDomain\",\"logCategory\":\"mapi_vips-mobile-login\",\"host\":\"GD9-sapi-054\",\"path\":\"/apps/logs/tomcat/vips-mobile/vips-mobile-login.log\",\"logLevel\":\"WARN\",\"logger\":\"MobileUserServiceExtImpl\",\"eventType\":\"get user id error\"}";

    private MsgEvent           msg;

    @Before
    public void init() {
        msg = new MsgEvent(testMsg);
    }
    
    @Test
    public void test1() throws Exception {
    	String expr = "1 && \"true\" && !\"0\" && !\"fasle\"";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    
    @Test
    public void test2() throws Exception {
    	String expr = "1<2 && 3<=4 && 4=4 && 6>=6 && 8>7";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    @Test
    public void test3() throws Exception {
    	String expr = "1 !=1 || 2<1 || 3<=2 || 4>5 || 6>=7";
    	EventFilter filter = new EventFilter(expr);
    	assertFalse(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    @Test
    public void test4() throws Exception {
    	String expr = "0*1<1 && 1*2 < 2*2 && 2+3<6 && 4/5<1";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    
    @Test
    public void test5() throws Exception {
    	String expr = "1+1=2 && 1-1=0 && 1/1=1 && 1*1=1";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    
    @Test
    public void test6() throws Exception {
    	String expr = "0<1 || 1<2";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }

    @Test
    public void testLog() throws Exception {
    	MsgEvent msg = new MsgEvent("projectName=testProject timestamp=2017-06-06T12:13:14 host=Host123 num=20 deno=80 @version=1 ");
        String expr = "@ host && host=\"Host123\" && projectName # \"test\" && num/deno*100>20 && @version=1 && timestamp ~ \"^\\d{4}\"";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
    }
    
    @Test
    public void testFun() throws Exception {
    	MsgEvent msg = new MsgEvent("projectName=testProject timestamp=2017-06-06T12:13:14 host=Host123 num=20 deno=80 version=1 ");
        String expr = "sum(version) >= 1 && count() >= 1 && avg(version) >= 1 && min(version) >= 1 && max(version) >= 1";
        EventTrigger trigger = new EventTrigger(expr, null, pastTime);
        assertTrue(trigger.trigger(msg.getMappedMsg(), msg.getSeconds()));
    }
    
    @Test
	public void testPlug() throws Exception {
    	MsgEvent msg = new MsgEvent("projectName=testProject timestamp=2017-06-06T12:13:14 exePath=/home/wangchen exeUser=wangchen");
    	String expr = "exePath=\"/home/\"+exeUser && exePath - exeUser=\"/home/\"";
    	EventFilter filter = new EventFilter(expr);
    	assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
	}
}
