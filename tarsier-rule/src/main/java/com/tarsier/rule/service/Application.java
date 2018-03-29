package com.tarsier.rule.service;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tarsier.rule.engine.EngineHolder;
import com.tarsier.rule.engine.RuleHolder;
import com.tarsier.rule.source.Source;

@Service
public class Application {
	private static final Logger	LOGGER	= LoggerFactory.getLogger(Application.class);
	@Value("${service.running.default:true}")
	private boolean				runInit;
	private final AtomicBoolean	running	= new AtomicBoolean(false);
	@Autowired
	private RuleHolder			ruleHolder;
	@Autowired
	private EngineHolder		engineHolder;
	@Autowired
	private List<Source>		sources;
	@Autowired
	private MsgDispatchService	service;
	@Autowired
	private AlarmService	alarmService;
	private Timer				timer;
	private TimerTask			timerTask;

	// 本地缓存 监听的topic 信息
	@PostConstruct
	private void schedule() {
		if (runInit) {
			start();
		}
		timer = new Timer("check-rules", true);
		timerTask = new TimerTask() {

			@Override
			public void run() {
				if (running.get()) {
					try {
						sync();
					}
					catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
			
		};
		timer.schedule(timerTask, 60 * 1000, 60 * 1000);
	}
	
	public synchronized void sync() {
		ruleHolder.pullRule();
		ruleHolder.effectRule();
		if (engineHolder.channelsChanged()) {
			Set<String> channels = engineHolder.getChannels();
			for (Source s : sources) {
				s.restart(channels);
			}
		}
	}

	public synchronized void start() {
		if (!running.get()) {
			LOGGER.info("start...");
			service.start();
			ruleHolder.start();
			engineHolder.start();
			alarmService.start();
			for (Source s : sources) {
				s.setQueue(service.getQueue());
				s.start(engineHolder.getChannels());
			}
			running.set(true);
		}
	}

	public synchronized void stop() {
		if (running.compareAndSet(true, false)) {
			LOGGER.info("stop...");
			for (Source s : sources) {
				s.stop();
			}
			ruleHolder.stop();
			engineHolder.stop();
			alarmService.stop();
			service.stop();
		}
	}
}
