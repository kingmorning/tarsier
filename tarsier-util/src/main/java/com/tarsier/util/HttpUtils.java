package com.tarsier.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 类HttpUtils.java的实现描述：Http请求工具类。
 * 
 * @author wangchenchina@hotmail.com 2016年12月22日 下午6:37:41
 */
public class HttpUtils {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(HttpUtils.class);
	private static final RequestConfig requestConfig;
	static{
		requestConfig = RequestConfig.custom()    
				.setConnectTimeout(5000).setConnectionRequestTimeout(5000)    
				.setSocketTimeout(5000).build();    
	}
	public static List<NameValuePair> getParam(Map<String, String> parameterMap) {
		List<NameValuePair> param = new ArrayList<NameValuePair>();
		Iterator<Map.Entry<String, String>> it = parameterMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> parmEntry = (Entry<String, String>) it.next();
			param.add(new BasicNameValuePair((String) parmEntry.getKey(), (String) parmEntry.getValue()));
		}
		return param;
	}

//	public static HttpClient login(HttpClient client, String url, String userName, String password) {
//		try {
//			HttpPost httpPost = new HttpPost(url);
//			Map<String, String> parameterMap = new HashMap<String, String>();
//			parameterMap.put("j_username", userName);
//			parameterMap.put("j_password", password);
//			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
//			httpPost.setEntity(postEntity);
//			LOGGER.info("login request line:" + httpPost.getRequestLine());
//			// 执行post请求
//			HttpResponse httpResponse = client.execute(httpPost);
//			if (LOGGER.isDebugEnabled()) {
//				HttpEntity entity = httpResponse.getEntity();
//				LOGGER.debug("login response:{}", EntityUtils.toString(entity, Charset.forName("UTF-8")));
//			}
//			String location = httpResponse.getFirstHeader("Location").getValue();
//			if (location != null && location.endsWith("/cmf/login")) {
//				throw new RuntimeException("login fail. url:" + url + ", userName:" + userName);
//			}
//		}
//		catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
//		return client;
//	}

	public static String postForm(HttpClient client, String url, Map<String, String> parameterMap) {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(parameterMap), "UTF-8");
			httpPost.setEntity(postEntity);
			LOGGER.debug("post request line:{}", httpPost.getRequestLine());
			LOGGER.debug("post data:{}", getParam(parameterMap));
			// 执行post请求
			HttpResponse httpResponse = client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code < 200 || code >= 300) {
				LOGGER.warn("http code is:{}, url:{}", code, url);
			}
			HttpEntity entity = httpResponse.getEntity();
			return EntityUtils.toString(entity, Charset.forName("UTF-8"));
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static String postJson(HttpClient client, String url, JSONObject json) {
		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setConfig(requestConfig);
			StringEntity str= new StringEntity(JSON.toJSONString(json), Charset.forName("UTF-8"));
			httpPost.setEntity(str);
			httpPost.setHeader("Content-type", "application/json;charset=utf-8");
			LOGGER.debug("post request line:{}", httpPost.getRequestLine());
			LOGGER.debug("post data:{}", JSON.toJSONString(json));
			// 执行post请求
			HttpResponse httpResponse = client.execute(httpPost);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code < 200 || code >= 300) {
				LOGGER.warn("http code is:{}, url:{}", code, url);
				return null;
			}
			HttpEntity entity = httpResponse.getEntity();
			return EntityUtils.toString(entity, Charset.forName("UTF-8"));
		}
		catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	public static String get(HttpClient client, String url) throws IOException{
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		LOGGER.debug("get request line:" + httpGet.getRequestLine());
		HttpResponse resp;
//		try {
			resp = client.execute(httpGet);
			int code = resp.getStatusLine().getStatusCode();
			if (code < 200 || code >= 300) {
				LOGGER.warn("http code is:{}, url:{}", code, url);
			}
			HttpEntity entity = resp.getEntity();
			return EntityUtils.toString(entity, Charset.forName("UTF-8"));
//		}
//		catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
//		return null;
	}

	public static HttpResponse getResp(HttpClient client, String url) throws ClientProtocolException, IOException {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(requestConfig);
		LOGGER.debug("get request line:" + httpGet.getRequestLine());
		return client.execute(httpGet);
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
				// 信任所有
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return HttpClients.createDefault();
	}
}
