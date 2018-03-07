package com.tarsier.flume;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.ConfigurationException;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.http.HTTPSourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlobHandler implements HTTPSourceHandler {

	  private int maxBlobLength = MAX_BLOB_LENGTH_DEFAULT;

	  public static final String MAX_BLOB_LENGTH_KEY = "maxBlobLength";
	  public static final int MAX_BLOB_LENGTH_DEFAULT = 100 * 1000 * 1000;

	  private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
	  private static final Logger LOGGER = LoggerFactory.getLogger(BlobHandler.class);

	  public BlobHandler() {
	  }

	  @Override
	  public void configure(Context context) {
	    this.maxBlobLength = context.getInteger(MAX_BLOB_LENGTH_KEY, MAX_BLOB_LENGTH_DEFAULT);
	    if (this.maxBlobLength <= 0) {
	      throw new ConfigurationException("Configuration parameter " + MAX_BLOB_LENGTH_KEY
	          + " must be greater than zero: " + maxBlobLength);
	    }
	  }
	  
	  @SuppressWarnings("resource")
	  @Override
	  public List<Event> getEvents(HttpServletRequest request) throws Exception {
	    Map<String, String> headers = getHeaders(request);
	    InputStream in = request.getInputStream();
	    try {
	      ByteArrayOutputStream blob = null;
	      byte[] buf = new byte[Math.min(maxBlobLength, DEFAULT_BUFFER_SIZE)];
	      int blobLength = 0;
	      int n = 0;
	      while ((n = in.read(buf, 0, Math.min(buf.length, maxBlobLength - blobLength))) != -1) {
	        if (blob == null) {
	          blob = new ByteArrayOutputStream(n);
	        }
	        blob.write(buf, 0, n);
	        blobLength += n;
	        if (blobLength >= maxBlobLength) {
	          LOGGER.warn("Request length exceeds maxBlobLength ({}), truncating BLOB event!", maxBlobLength);
	          break;
	        }
	      }

	      byte[] array = blob != null ? blob.toByteArray() : new byte[0];
	      Event event = EventBuilder.withBody(array, headers);
	      LOGGER.debug("blobEvent: {}", event);
	      return Collections.singletonList(event);
	    } finally {
	      in.close();
	    }
	  }

	  private Map<String, String> getHeaders(HttpServletRequest request) {
	    Map<String, String> headers = new HashMap();
	    String uris = request.getQueryString();
	    for(String param : uris.split("&")){
	    	String[] kv = param.split("=", 2);
	    	if(kv.length>1){
	    		headers.put(kv[0], kv[1]);
	    	}
	    }
	    return headers;
	  }

}
