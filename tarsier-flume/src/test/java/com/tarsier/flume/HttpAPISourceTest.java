package com.tarsier.flume;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

public abstract class HttpAPISourceTest {
	protected HttpClient	client	= new DefaultHttpClient(new ThreadSafeClientConnManager());
}
