/*
 * Copyright 2016-2020 KingMorning All right reserved. This software is the confidential and proprietary information of
 * KingMorning ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with KingMorning.
 */
package com.tarsier.rule.source;


/**
 * 类KafkaTest.java的实现描述：
 * 
 * @author wangchenchina@hotmail.com 2016年4月15日 下午4:47:18
 */
public class KafkaTest {
	protected static String	servers				= "ctum2e1502002:9092,ctum2e1502001:9092,ctum2e1602001:9092,ctum2e1802001:9092,ctum2e1702001:9092";
//	protected static String	servers				= "testbig10:9092,testbig11:9092";
	// protected static String servers = "10.213.128.101:9092";
	// protected static String servers = "10.213.129.41:10092";
	protected static String	zookeeperConnect	= "testbig3,testbig4,testbig5/kafka1";
	// protected static String zookeeperConnect = "10.213.129.41:10021";
	protected static String	topic				= "lineage";
//	protected static String	topic				= "test";
	// protected static String topic = "drManager";

	protected static String	groupId				= "testConsumer1";
}
