package com.tarsier.antlr;

import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tarsier.antlr.RuleParser.StatContext;
import com.tarsier.antlr.exception.EventFilterException;
import com.tarsier.antlr.function.Function;
import com.tarsier.antlr.function.FunctionParser;
import com.tarsier.antlr.groupby.GroupBy;

public class EventTrigger {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventTrigger.class);

	private final RuleParser parser;

	private final GroupBy groupby;

	private final String expression;

	/**
	 * @param expression 触发器 表达式
	 * @param groupField 分组值
	 * @param timewin 时间窗口，单位秒
	 */
	public EventTrigger(String expression, String groupField, int pastSecond) {
		if (StringUtils.isBlank(expression)) {
			parser = null;
			this.expression = null;
			groupby = null;
			LOGGER.warn("trigger expression is empty.");
		} else {
			RuleLexer lexer = new RuleLexer(new ANTLRInputStream(expression));
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new RuleParser(tokens);
			this.expression = expression;
			LOGGER.debug("paser expression:{} success.", expression);

			List<Function> functions = FunctionParser.buildFunctions(expression);
			if (!functions.isEmpty()) {
				this.groupby = new GroupBy(pastSecond, groupField, functions);
				parser.setPastSecond(pastSecond);
				LOGGER.debug("parse group by:{}", groupby.toString());
			} else {
				this.groupby = null;
			}
		}
	}

	public boolean trigger(Map<String, String> msg, long second) throws EventFilterException {
		try {
			if (groupby != null ) {
				if(msg != null){
					groupby.groupBy(msg, second);
				}
				parser.setCache(groupby.getSubCache(msg));
			}
			parser.setMsg(msg, second);
			StatContext stat = parser.stat();
			if (LOGGER.isDebugEnabled()) {
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

	public GroupBy getGroupby() {
		return groupby;
	}

	public String getExpression() {
		return expression;
	}

	public Map<String, Object> getFunctionMap() {
		return parser.functionMap;
	}

}
