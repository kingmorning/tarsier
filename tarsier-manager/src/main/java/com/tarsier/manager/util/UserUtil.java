package com.tarsier.manager.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUtil {
	public static final Logger LOG = LoggerFactory.getLogger(UserUtil.class);
	private static String defaultUser = "root";

//	public static String userName(String userName) {
//		return StringUtils.isBlank(userName) && LOG.isDebugEnabled() ? defaultUser : userName;
//	}
	
//	public static String getDefaultConf() {
//		return "input{\n\tfile { \n\t\tstart_position=>\"beginning\"\n\t\tpath=>[\"/tmp/test.log\"]}\n}\noutput{\n\tstdout{\n\t\tcodec => rubydebug\n\t}\n}";
//	}
	public static boolean isAdmin(String userName){
		return userName !=null && (userName.equals("root") || userName.equals("elastic"));
	}
}
