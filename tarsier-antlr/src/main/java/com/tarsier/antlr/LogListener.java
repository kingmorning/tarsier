package com.tarsier.antlr;

import java.lang.reflect.Field;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogListener extends RuleBaseListener {
	private static final Logger LOG = LoggerFactory.getLogger(LogListener.class);

	@Override
	public void exitEveryRule(ParserRuleContext ctx) {
		if(LOG.isDebugEnabled()){
			Class c = ctx.getClass();
			try {
				Field vf = c.getDeclaredField("value");
				LOG.debug("type:{}, text:{}, value:{}",c.getSimpleName(), ctx.getText(), vf.get(ctx));
			} catch (Exception ignore) {
				LOG.warn("no value field.");
			}
		}
	}

}
