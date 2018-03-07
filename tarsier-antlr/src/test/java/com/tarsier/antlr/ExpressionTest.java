package com.tarsier.antlr;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import com.tarsier.antlr.EventFilter;
import com.tarsier.data.LoggerMsg;

public class ExpressionTest {

	// private String testMsg =
	// "{\"BaiduMapLoadCostTime\":\"1501毫秒\",\"IMEI\":\"860670025684431\",\"OS\":\"android\",\"logLevel\":\"error\",\"@timestamp\":\"2016-01-28T18:35:38\",\"projectName\":\"kmAndroidApp\",\"hostName\":\"101.80.194.105\"}";

	private LoggerMsg	msg;

	@Test
	public void test() throws Exception {
		String testMsg = "{\"timestamp\":\"2016-04-06T13:40:35\",\"logLevel\":\"ERROR\",\"projectName\":\"km-pc\",\"IMEI\":\"\",\"OS\":\"web\",\"type\":\"http-success\",\"http\":{\"status\":200,\"url\":\"/getPathInfo.action?g=3&id=126045&_=1459920968930\",\"type\":\"GET\",\"contentType\":\"application/x-www-form-urlencoded; charset=UTF-8\",\"dataType\":\"json\",\"totalTime\":46},\"logTimeProject\":\"2016-04-06T13:40:35\",\"hostName\":\"219.142.76.10\"}";
		msg = new LoggerMsg(testMsg);
		String expr = "http.status=200";
		EventFilter filter = new EventFilter(expr);
		assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
	}

	@Test
	public void test1() throws Exception {
		String testMsg = "ip=10.159.63.104   createTime=1471420813000    spendTime=166   method=GET  url=/houseMarkByLevel.action?lngF=121.526668&lngT=121.555414&latF=31.186833&latT=31.20041&p=2&ht=1&zoom=17&eid=6526&_=1471397112421 protocol=HTTP/1.0   statusCode=200  responseSize=3402   ref=https://www.com/chuzu/map/grade3areaId6526lon121.54528lat31.191939/?kw=%E9%87%91%E5%9C%B0%E5%9F%8E agent=Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0  timestamp=2016-08-17T16:00:13 logType=access  projectName=km-user-web   hostName=km-user-web5";
		msg = new LoggerMsg(testMsg);
		String expr = "statusCode=200";
		EventFilter filter = new EventFilter(expr);
		assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
	}
	
	@Test
	public void test2() throws Exception {
		String testMsg = "{\"path\":\"/var/log/messages\",\"system_time\":\"Jun 30 09:54:02\",\"@timestamp\":\"2017-06-30T01:54:03.575Z\",\"log\":\"the clock difference against peer 61b323a63231c979 is too high [29.14368678s > 1s]\",\"service\":\"etcd\",\"@version\":\"1\",\"host\":\"ctum2a2402007\",\"type\":\"system\",\"projectName\":\"etcd\"}";
		msg = new LoggerMsg(testMsg);
		System.out.println(msg.getTime());
	}
	
	@Test
	public void test3() throws Exception {
		String testMsg = "taskmanagers=1 jobs-running=2 @timestamp=2017-06-30T01:54:03.575Z";
		msg = new LoggerMsg(testMsg);
		String expr = "jobs-running=2";
		EventFilter filter = new EventFilter(expr);
		assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
		System.out.println(msg.getTime());
	}
	
	@Test
	public void test4() throws Exception {
		String testMsg = "taskmanagers=1 jobs-running=2 @timestamp=2017-06-30T01:54:03.575Z";
		msg = new LoggerMsg(testMsg);
		String expr = "@ database ||!@ kafka || responseTime > 1000";
		EventFilter filter = new EventFilter(expr);
		assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
		System.out.println(msg.getTime());
	}
	
	@Test
	public void test5() throws Exception {
		String testMsg = "taskmanagers=1 jobs-running=2 @timestamp=2017-06-30T01:54:03.575Z";
		msg = new LoggerMsg(testMsg);
		String expr = "!@ database || taskmanagers !# \"2\" ";
		EventFilter filter = new EventFilter(expr);
		assertTrue(filter.filter(msg.getMappedMsg(), msg.getSeconds()));
		System.out.println(msg.getTime());
	}
}
