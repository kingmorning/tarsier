package com.tarsier.rule.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.tarsier.rule.util.RuleUtil;

public class TestRuleUtil {

	@Test
	public void test() {
		 Map<String, String> map = new HashMap<String, String>();
		 map.put("jobsrunning", "0");
		 map.put("jobs-running", "1");
		 map.put("jobs.running", "2");
		 map.put("jobs@running", "3");
		 map.put("jobs_running", "4");
		String rp = RuleUtil.replaceParam(map, "start:${jobsrunning},${jobs-running},${jobs.running},${jobs@running},${jobs_running} end。");
		System.out.println(rp);
		assertEquals("start:0,1,2,3,4 end。", rp);
	}

}
