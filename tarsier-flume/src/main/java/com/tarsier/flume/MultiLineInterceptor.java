package com.tarsier.flume;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.interceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.flume.MultiLineSource.RefreshEvent;

public class MultiLineInterceptor implements Interceptor {
	
	private static final Logger LOG = LoggerFactory.getLogger(MultiLineInterceptor.class);

	private String				pattern;
	private Pattern				compile;
	private final List<Event>	cachedEvents	= new ArrayList<Event>();
	private Event latest=new SimpleEvent();

	public MultiLineInterceptor(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public void initialize() {
		compile = Pattern.compile(pattern);
	}

	@Override
	public Event intercept(Event event) {
		Event e = null;
		boolean refresh = (event instanceof RefreshEvent);
		if(refresh){
			if(cachedEvents.size()>0 && latest instanceof RefreshEvent){
				LOG.debug("refresh cached events. as limited time.");
				e= mergeEvents();
			}
		}
		else if (event.getBody().length > 0){
			String msg = new String(event.getBody());
			if (compile.matcher(msg).find() || cachedEvents.size() >= 500) {
				LOG.debug("refresh cached events. as new valid event.");
				e = mergeEvents();
			}
			cachedEvents.add(event);
		}
		latest=event;
		return e;
	}

	private Event mergeEvents() {
		int size = cachedEvents.size();
		if (size == 0) {
			return null;
		}
		else {
			Event event = cachedEvents.get(0);
			StringBuilder s = new StringBuilder();
			Map<String, String> headers = event.getHeaders();
			for (String k : headers.keySet()) {
				s.append(k).append("=").append(headers.get(k)).append(" ");
			}
			for (int i = 0; i < size; i++) {
				Event e = cachedEvents.get(i);
				if (i > 0) {
					s.append("\n");
				}
				s.append(new String(e.getBody()));
			}
			event.setBody(s.toString().getBytes());
			event.getHeaders().clear();
			cachedEvents.clear();
			return event;
		}
	}

	@Override
	public List<Event> intercept(List<Event> events) {
		List<Event> es = new ArrayList<Event>();
		for (Event event : events) {
			Event e = intercept(event);
			if (e != null) {
				es.add(e);
			}
		}
		return es;
	}

	@Override
	public void close() {

	}

	public static class Builder implements Interceptor.Builder {
		private MultiLineInterceptor	i;

		@Override
		public void configure(Context context) {
			String pattern = context.getString("pattern", "^\\s*[\\d]{4}[^0-9].*");
			LOG.info("MultiLineInterceptor pattern:{}", pattern);
			i = new MultiLineInterceptor(pattern);
		}

		@Override
		public Interceptor build() {
			return i;
		}
	}
}
