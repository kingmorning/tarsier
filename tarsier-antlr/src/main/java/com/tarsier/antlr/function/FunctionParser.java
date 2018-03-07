package com.tarsier.antlr.function;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionParser {
	public static String	AVG					= "avg";
	public static String	COUNT				= "count";
	public static String	MIN					= "min";
	public static String	MAX					= "max";
	public static String	SUM					= "sum";
	public static List<Function> buildFunctions(String triggerExpr) {
		List<Function> fs = new ArrayList<Function>();
		Set<String> fNames = new HashSet<String>();
		getFunction(0, triggerExpr, fNames);
		for (String fName : fNames) {
			String lowName = fName.toLowerCase();
			if (lowName.startsWith(SUM)) {
				fs.add(new Sum(fName));
			} else if (lowName.startsWith(COUNT)) {
				fs.add(new Count(fName));
			} else if (lowName.startsWith(MAX)) {
				fs.add(new Max(fName));
			} else if (lowName.startsWith(MIN)) {
				fs.add(new Min(fName));
			} else if (lowName.startsWith(AVG)) {
				fs.add(new Sum(SUM + "_" + fName));
				fs.add(new Count(COUNT + "_" + fName));
			}
		}
		return fs;
	}

	private static void getFunction(int i, String triggerExpr, Set<String> ns) {
		if (i >= 0 && i < triggerExpr.length()) {
			Pattern p = Pattern
					.compile("(\\w)+(\\s)*\\((\\s)*(@|\\w)*(\\s)*\\)");
			Matcher m = p.matcher(triggerExpr);
			if (m.find(i)) {
				String fName = m.group(0).replaceAll(" ", "");
				ns.add(fName);
				i = m.end();
				getFunction(i, triggerExpr, ns);
			}
		}
	}
}
