package com.tarsier.flume;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

import com.tarsier.flume.ClouderaNavigatorSource;
import com.tarsier.flume.HttpAPISource;

public class ClouderaNavigatorSourceTest extends HttpAPISourceTest {

	public String	baseURL	= "http://10.214.128.50:7187/api/v9/audits?query=operation_text==*select*&limit=1000&offset=0&format=JSON&attachment=false&";
	Header			auth	= new BasicHeader("Authorization", "Basic "
									+ new String(Base64.encodeBase64("heyu16:heyu16".getBytes())));

	@Test
	public void test() throws Exception {
		String params = "&startTime=1487229043633&endTime=1487230043633";
		System.out.println(HttpAPISource.get(client, baseURL + params, auth));
	}

	@Test
	public void testName() throws Exception {
		ClouderaNavigatorSource c = new ClouderaNavigatorSource();
		Map<String, String> param = new HashMap<String, String>();
		param.put("baseURL", "http://10.214.128.50:7187");
		param.put("apis",
				"/api/v9/audits?query=operation_text==*select*&limit=1000&offset=0&format=JSON&attachment=false&");
		param.put("userName", "heyu16");
		param.put("password", "heyu16");
		c.configure(new Context(param));
		c.setChannelProcessor(new ChannelProcessor(null) {
			@Override
			public void processEvent(Event event) {
				System.err.println(new String(event.getBody()));
			}
		});
		c.start();
		Thread.currentThread().sleep(50000);
	}
}
