package com.tarsier.rule.proxy;

import java.util.List;

import org.junit.Test;

import com.tarsier.rule.proxy.HttpRuleProxy;
import com.tarsier.util.Rule;

public class HttpRuleProxyTest {

	@Test
	public void testName() throws Exception {
		HttpRuleProxy p=new HttpRuleProxy();
		p.setManagerServer("http://127.0.0.1:8080/tarsier-manager/api");
		List<Rule> rules = p.listEnabled();
		System.out.println("rule size:"+rules.size());
	}
}
