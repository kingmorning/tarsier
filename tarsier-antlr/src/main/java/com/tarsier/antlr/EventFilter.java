package com.tarsier.antlr;

import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.antlr.RuleParser.StatContext;
import com.tarsier.antlr.exception.EventFilterException;

public class EventFilter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EventFilter.class);

	private final RuleParser parser;

	private final String expression;

	public EventFilter(String expression) {
		this.expression = expression;
		if (!StringUtils.isBlank(expression)) {
			RuleLexer lexer = new RuleLexer(new ANTLRInputStream(expression));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new RuleParser(tokens);
		    LOGGER.debug("paser expression:{} success.", expression);
		} else {
			parser = null;
			LOGGER.warn("expression is empty.");
		}
	}

	public boolean filter(Map<String, String> msg, long second) throws EventFilterException {
		try {
			parser.setMsg(msg, second);
			StatContext stat = parser.stat();
			if(LOGGER.isDebugEnabled()){
				ParseTreeWalker walker = new ParseTreeWalker();
				LogListener listener = new LogListener();
				walker.walk(listener, stat);
				LOGGER.debug("filter msg by {}, result:{}.", expression, stat.value);
			}
			return stat.value;
		} catch (Exception e) {
			throw new EventFilterException(e.getMessage() + ". expression:" + expression + ". log:" + msg, e);
		} finally {
			parser.resetInput();
		}
	}

	public String getExpression() {
		return expression;
	}

}
