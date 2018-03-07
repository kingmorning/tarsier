package com.tarsier.rule.proxy;

import org.junit.BeforeClass;
import org.junit.Test;

import com.tarsier.rule.data.AlarmInfo;
import com.tarsier.rule.proxy.AlarmSystemProxyImpl;

public class AlarmSystemProxyTest {
	private static AlarmSystemProxyImpl	proxy	= null;

	@BeforeClass
	public static void setup() {
		proxy = new AlarmSystemProxyImpl();
		proxy.start();
	}

	@Test
	public void testSms() throws Exception {
		AlarmInfo ai = new AlarmInfo(2);
		ai.setMobiles("13774307090");
		ai.setMessage("测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容sdfds测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容测试\n内容sdfewrwer");
		proxy.sendSms(ai);
	}

	@Test
	public void testMail() throws Exception {
		AlarmInfo ai = new AlarmInfo(1);
		ai.setEmails("wangchenchina@hotmail.com");
		ai.setMessage("this is test message 中文.\n\r中文");
		ai.setLog("log内容");
		proxy.sendMail(ai);
	}

	@Test
	public void testWeChat() throws Exception {
		AlarmInfo ai = new AlarmInfo(4);
		ai.setMessage("this is test message 中文.\n\r中文");
		ai.setLog("log内容");
		ai.setPersons("wangchen");
		proxy.sendWeChat(ai);
	}
}
