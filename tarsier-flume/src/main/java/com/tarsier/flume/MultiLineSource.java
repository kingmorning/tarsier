package com.tarsier.flume;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.ExecSource;

public class MultiLineSource extends ExecSource {
	public class RefreshEvent  extends SimpleEvent{
		
	}
	
	private boolean run=false;
	private Timer					timer;
	private TimerTask				timerTask;
	@Override
	public synchronized void start() {
		super.start();
		run=true;
		timer = new Timer("generate-refreshEvent", true);
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!run) {
					return;
				}
				try {
					getChannelProcessor().processEvent(new RefreshEvent());
				}
				catch (Exception ignore) {
				}
			}
		};
		timer.schedule(timerTask, 5 * 1000, 5 * 1000);
	}

	public synchronized void stop() {
		super.stop();
		run=false;
	};
}
