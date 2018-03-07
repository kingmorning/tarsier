package com.tarsier.util.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import com.tarsier.util.Constant;

/**
 * 类IpCheckAPIFilter.java的实现描述：IP地址过滤，可通过配置${allow.ips}来配置指定的ip地址区间 来访问告警系统。
 * 
 * @author wangchen.sh 2016年1月30日 上午11:25:20
 */
public class IpCheckAPIFilter implements Filter {

	private Logger		logger		= LoggerFactory.getLogger(IpCheckAPIFilter.class);

	@Value("${allow.ips}")
	private String		allowIPs;

	private Set<String>	ips			= null;

	private Set<String>	ipSegments	= null;

	public void init(FilterConfig filterConfig) throws ServletException {
		ips = new HashSet<String>();
		ipSegments = new HashSet<String>();
		if (StringUtils.isNotBlank(allowIPs)) {
			ips.clear();
			ipSegments.clear();
			String[] ipss = allowIPs.split(Constant.COMMA);
			for (String ip : ipss) {
				ip = ip.trim();
				if (ip.indexOf('-') > 0) {
					ipSegments.add(ip);
				}
				else {
					ips.add(ip);
				}
			}
		}
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		if (ips == null || ipSegments == null) {
			init(null);
		}
		String rip = httpReq.getHeader(Constant.X_FORWARDED_FOR);
		if (StringUtils.isBlank(rip)) {
			rip = httpReq.getRemoteAddr();
		}
		if (StringUtils.isBlank(rip)) {
			processIpAccessDeniedResponse("remote ip is empty.", httpRes);
			return;
		}
		String[] rips = rip.split(Constant.COMMA);
		boolean pass = false;
		for (String ip : rips) {
			if (!pass && !ips.isEmpty()) {
				pass = ips.contains(ip);
			}

			if (!pass && !ipSegments.isEmpty()) {
				for (String ipSegment : ipSegments) {
					if (ipIsValid(ipSegment, ip)) {
						pass = true;
						break;
					}
				}
			}

			if (!pass) {
				logger.warn("IP {} has no privilege to access rule-alarm", ip);
				break;
			}
		}
		if (pass) {
			chain.doFilter(request, response);
		}
		else {
			String msg = String.format("%s can't contains ip:%s", allowIPs, Arrays.asList(rips).toString());
			logger.warn(msg);
			processIpAccessDeniedResponse(msg, httpRes);
		}
	}

	private void processIpAccessDeniedResponse(String msg, HttpServletResponse httpRes) {
		httpRes.setStatus(HttpStatus.FORBIDDEN.value());
		PrintWriter writer = null;
		try {
			writer = httpRes.getWriter();
			writer.print(msg);
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(writer);
		}
	}

	public void destroy() {

	}

	private boolean ipIsValid(String ipSection, String ip) {
		try {
			int idx = ipSection.indexOf('-');
			String[] sips = ipSection.substring(0, idx).split("\\.");
			String[] sipe = ipSection.substring(idx + 1).split("\\.");
			String[] sipt = ip.split("\\.");
			long ips = 0L, ipe = 0L, ipt = 0L;
			for (int i = 0; i < 4; ++i) {
				ips = ips << 8 | Integer.parseInt(sips[i]);
				ipe = ipe << 8 | Integer.parseInt(sipe[i]);
				ipt = ipt << 8 | Integer.parseInt(sipt[i]);
			}
			if (ips > ipe) {
				long t = ips;
				ips = ipe;
				ipe = t;
			}
			return ips <= ipt && ipt <= ipe;
		}
		catch (Exception e) {
			return false;
		}
	}

}
