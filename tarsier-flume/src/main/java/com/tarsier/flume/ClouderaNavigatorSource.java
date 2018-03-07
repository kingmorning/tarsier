package com.tarsier.flume;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.flume.Context;
import org.apache.flume.event.SimpleEvent;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;
@Deprecated
public class ClouderaNavigatorSource extends HttpAPISource {
	private static final Logger	LOGGER		= LoggerFactory.getLogger(ClouderaNavigatorSource.class);
	private static final String	startTime	= "startTime";
	private static final String	endTime		= "endTime";
	private Header				auth;
	private int					timespan;
	private long				preTime;
	@Override
	public void configure(Context context) {
		super.configure(context);
		timespan = context.getInteger("timespan", 5);
		interval = timespan * 60;
		preTime = System.currentTimeMillis() - interval * 1000;
		byte[] encodeBase64 = Base64.encodeBase64((userName + ":" + password).getBytes());
		auth = new BasicHeader("Authorization", "Basic " + new String(encodeBase64));
	}

	@Override
	protected void process(String api) throws Exception {
		Date nowTime = new Date();
		StringBuilder s = new StringBuilder(baseURL);
		s.append(api).append(startTime).append("=").append(preTime);
		s.append("&").append(endTime).append("=").append(nowTime.getTime());
		String msg = get(client, s.toString(), auth);
		if(msg !=null){
			List<Map<String, Object>> jsons = new ArrayList<Map<String, Object>>();
			Type type = new TypeToken<ArrayList<Map<String, Object>>>() {
			}.getType();
			jsons = gson.fromJson(msg, type);
			for (Map<String, Object> json : jsons) {
				String time = json.get(timestamp).toString().substring(0, 19);
				Calendar c = Calendar.getInstance();
				c.setTime(format.parse(time));
				c.add(Calendar.HOUR, 8);
				json.put(timestamp, format.format(c.getTime()));
				json.put(projectName, "navigator_select");
				json.putAll((Map)json.get("serviceValues"));
				json.remove("serviceValues");
				json.remove("operationText");
				SimpleEvent event = new SimpleEvent();
				event.setBody(gson.toJson(json).getBytes());
				getChannelProcessor().processEvent(event);
				sourceCounter.incrementEventAcceptedCount();
			}
			preTime = nowTime.getTime();
//			LOGGER.debug("api:{}, msg:{}", api, jsons);
		}
	}
}
