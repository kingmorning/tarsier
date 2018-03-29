package com.tarsier.rule.proxy;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tarsier.data.MsgEvent;
import com.tarsier.rule.data.AlarmEvent;
import com.tarsier.util.Rule;

public class AlarmProxyTest {

	private AlarmEvent getAE(){
		Rule r = new Rule();
		r.setId(1);
		r.setProjectName("project");
		r.setName("test rule engine");
		r.setWeekly(Rule.weeklyAll);
		r.setTimeRange(Rule.dailyAll);
		r.setTimewin(1);
		r.setInterval(30);
		r.setPersons("wangchen");
		r.setEmails("wangchenchina@hotmail.com");
		r.setMobiles("13774307090");
		r.setType(7);
		
		String msg = "timestamp=2018-03-04T17:01:02 message=XXXX YYYY projectName=log-test";
		AlarmEvent ae = new AlarmEvent(r, new MsgEvent(msg), r.getProjectName(), "defaultGroup");
		return ae;
	}
	
	@BeforeClass
	public static void setup() {
	}

	@Test
	public void testSms() throws Exception {
		SmsAlarmProxy proxy=new SmsAlarmProxy();
		proxy.init();
		String content="测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容sdfds测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容sdfewrwer";
		AlarmEvent ae = getAE();
		proxy.send(ae, content);
		proxy.destroy();
	}

	@Test
	public void testMail() throws Exception {
		EmailAlarmProxy proxy=new EmailAlarmProxy();
		String content="测试邮件，标题";
		AlarmEvent ae = getAE();
		proxy.send(ae, content);
	}

	@Test
	public void testWeChat() throws Exception {
		WeChatAlarmProxy proxy=new WeChatAlarmProxy();
		proxy.init();
		String content="微信内容测试";
		AlarmEvent ae = getAE();
		proxy.send(ae, content);
		proxy.destroy();
	}
	
	@Test
	public void testKafka() throws Exception {
		KafkaAlarmProxy proxy=new KafkaAlarmProxy();
		proxy.init();
		String content="微信内容测试";
		AlarmEvent ae = getAE();
		proxy.send(ae, content);
		proxy.destroy();
	}
}
