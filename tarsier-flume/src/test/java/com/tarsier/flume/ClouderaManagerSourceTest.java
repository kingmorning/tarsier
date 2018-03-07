package com.tarsier.flume;

import org.junit.Test;

import com.tarsier.flume.ClouderaManagerSource;
import com.tarsier.flume.HttpAPISource;

public class ClouderaManagerSourceTest extends HttpAPISourceTest {
	String	loginURL	= "http://10.214.128.31:7180/j_spring_security_check";
	String	getURL		= "http://10.214.128.31:7180//api/v12/clusters/Cluster1/services";
	String	username	= "admin";
	String	password	= "admin";

	@Test
	public void test() {
		ClouderaManagerSource.login(client, loginURL, username, password);
		String respo = HttpAPISource.get(client, getURL, null);
		System.out.print("respon is " + respo);
	}
}
