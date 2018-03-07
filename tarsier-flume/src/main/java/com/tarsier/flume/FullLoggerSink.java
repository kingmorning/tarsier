package com.tarsier.flume;

import org.apache.flume.Channel;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FullLoggerSink extends AbstractSink {
	private static final Logger LOG = LoggerFactory.getLogger(FullLoggerSink.class);
	@Override
	public Status process() throws EventDeliveryException {
	    Status result = Status.READY;
	    Channel channel = getChannel();
	    Transaction transaction = channel.getTransaction();
	    Event event = null;

	    try {
	      transaction.begin();
	      event = channel.take();

	      if (event != null) {
	        	LOG.info("Event: Header:{}, Body:{}", event.getHeaders(), new String(event.getBody()));
	      } else {
	        result = Status.BACKOFF;
	      }
	      transaction.commit();
	    } catch (Exception ex) {
	      transaction.rollback();
	      throw new EventDeliveryException("Failed to log event: " + event, ex);
	    } finally {
	      transaction.close();
	    }

	    return result;
	  }

}
