package com.tarsier.util;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.tarsier.data.LogParser;

public class TestUtil {
//	Type t = new TypeToken<Map<String, String>>(){}.getType();
//	Gson gson = new GsonBuilder().registerTypeAdapter(t, new FlattenDeserializer()).create();
//
//	class FlattenDeserializer implements JsonDeserializer<Map<String, String>> {
//	    @Override
//	    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//	        Map<String, String> map = new LinkedHashMap<>();
//	        desc(json, typeOfT, map, null);
//	        return map;
//	    }
//	    
//	    private void desc(JsonElement json, Type typeOfT, Map<String, String> map, String path){
//	    	if (json.isJsonArray()) {
//	    		if(path ==null){
//	    			throw new IllegalArgumentException("unsupport jsonarray. input:"+json.toString());
//	    		}
//	    		int i=1;
//	            for (JsonElement e : json.getAsJsonArray()) {
//	                desc(e, typeOfT, map, path+"."+i++);
//	            }
//	        } else if (json.isJsonObject()) {
//	            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
//	            	String key = path==null ? entry.getKey() : (path+"."+entry.getKey());
//	                if (entry.getValue().isJsonPrimitive()) {
//	                    map.put(key, entry.getValue().getAsString());
//	                } else {
//	                	desc(entry.getValue(), typeOfT, map, key);
//	                }
//	            }
//	        }
//	        else if(json.isJsonPrimitive()){
//	        	map.put(path, json.getAsString());
//	        }
//	        else if(json.isJsonNull()){
//	        	map.put(path, "null");
//	        }
//	    }
//	}
//	
//	@Test
//	public void testName() throws Exception {
//		Date date = DateUtil.DATE_TIME_TZ.parse("2017-07-11T03:10:08.238Z");
//		System.out.println(date.getTime());
//	}
//	
//	@Test
//	public void test1() throws Exception {
//		String m = "{\"cc\":[\"v1\",\"v2\"],\"dd\":null}";
//		Map<String, String> map = gson.fromJson(m, t);
//		System.out.println(map);
//	}
//	
//	@Test
//	public void test2() throws Exception {
//		JsonObject jo = new JsonObject();
//		jo.addProperty("taskmanagers", 1);
//		jo.addProperty("jobs-running", 31);
//		String json = gson.toJson(jo);
//		Map<String, String> parse = LogParser.parse(json);
//		System.out.println(parse);
//	}
}
