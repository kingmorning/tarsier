package com.tarsier.flume;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KafkaAvroTest {

	private List<String>							topics	= Arrays.asList("maiWaiDi");
	private static KafkaConsumer<String, byte[]>	consumer;
	@BeforeClass
	public static void initConsumer() {
		if (consumer == null) {
			Properties props = new Properties();
			props.put("bootstrap.servers", "10.214.160.222:9092");
			props.put("group.id", "avroTest");
			props.put("enable.auto.commit", "true");
			props.put("auto.commit.interval.ms", "1000");
			props.put("session.timeout.ms", "30000");
			props.put("auto.offset.reset", "earliest");
			props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
			props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
			consumer = new KafkaConsumer<String, byte[]>(props);
		}
	}

	@Test
	public void test() throws InterruptedException {
		consumer.subscribe(topics);
		System.out.println("topics:" + topics);
		Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroFlumeEvent\",\"namespace\":\"org.apache.flume.source.avro\",\"fields\":[{\"name\":\"headers\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"body\",\"type\":\"bytes\"}]}");
		while (true) {
			Thread.currentThread().sleep(1000);
			ConsumerRecords<String, byte[]> records = consumer.poll(10);
			System.err.println("records count:" + records.count());
			for (ConsumerRecord<String, byte[]> record : records) {
				DatumReader<GenericRecord> reader = new SpecificDatumReader<GenericRecord>(schema);
				Decoder decoder = DecoderFactory.get().binaryDecoder(record.value(), null);
                GenericRecord payload2 = null;
                try {
					payload2 = reader.read(null, decoder);
				} catch (IOException e) {
					e.printStackTrace();
				}
                System.out.println("headers is : " + payload2);
                
                StringBuilder body=new StringBuilder();
                ByteBuffer bytes = (ByteBuffer)payload2.get("body");
                for (int i = bytes.position(); i < bytes.limit(); i++)
                	body.append((char)bytes.get(i));
                System.err.println("body is: " + body);
			}
		}
	}
	
	@AfterClass
	public static void destroy() {
		if (consumer != null) {
			consumer.close();
			System.out.println("shut down consumer.");
		}
	}
}
