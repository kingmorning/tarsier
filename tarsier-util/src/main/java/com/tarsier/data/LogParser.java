package com.tarsier.data;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class LogParser {
	private static final Logger			LOG				= LoggerFactory.getLogger(LogParser.class);
	private static final char			decollator		= '=';
	private static final Set<Character>	terminals		= new HashSet<Character>(Arrays.asList(' ', '\t', '\n'));
	private static final Type token = new TypeToken<Map<String, String>>(){}.getType();
	private static final Gson gson = new GsonBuilder().registerTypeAdapter(token, new FlattenDeserializer()).create();

	static class FlattenDeserializer implements JsonDeserializer<Map<String, String>> {
	    @Override
	    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	        Map<String, String> map = new LinkedHashMap<>();
	        desc(json, typeOfT, map, null);
	        return map;
	    }
	    
	    private void desc(JsonElement json, Type typeOfT, Map<String, String> map, String path){
	    	if (json.isJsonArray()) {
	    		if(path ==null){
	    			throw new IllegalArgumentException("unsupport jsonarray. input:"+json.toString());
	    		}
	    		int i=1;
	            for (JsonElement e : json.getAsJsonArray()) {
	                desc(e, typeOfT, map, path+"."+i++);
	            }
	        } else if (json.isJsonObject()) {
	            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
	            	String key = path==null ? entry.getKey() : (path+"."+entry.getKey());
	                if (entry.getValue().isJsonPrimitive()) {
	                    map.put(key, entry.getValue().getAsString());
	                } else {
	                	desc(entry.getValue(), typeOfT, map, key);
	                }
	            }
	        }
	        else if(json.isJsonPrimitive()){
	        	map.put(path, json.getAsString());
	        }
	        else if(json.isJsonNull()){
	        	map.put(path, "null");
	        }
	    }
	}
	
	public static Map<String, String> parse(String str) {
			try {
				if (str.startsWith("{")) {
					return gson.fromJson(str, token);
				}
				else{
					Map<String, String> map = new LinkedHashMap<String, String>();
					kv(str, map);
					return map;
				}
			}
			catch (Exception ignore) {
				LOG.error("parse error:" + str, ignore);
			}
		return null;
	}

	private static void kv(String str, Map<String, String> map) {
		int preTmp;
		int prePos = -1;
		int pos = str.indexOf(decollator, prePos + 1);
		int tmp = prePos;
		int slicing = 0;
		String key = null;
		while (pos > 0) {
			preTmp = tmp > prePos ? tmp : prePos;
			tmp = pos;
			for (; tmp > 0 && tmp > prePos; tmp--) {
				Character c = str.charAt(tmp);
				if (terminals.contains(c) && pos - tmp > 1) {
					tmp++;
					break;
				}
			}

			if (tmp > preTmp) {
				if (key != null && key.length() > 0) {
					slicing = tmp;
					map.put(key.trim(), str.substring(prePos + 1, slicing).trim());
				}
				key = str.substring(slicing, pos);
				prePos = pos;
			}
			pos = str.indexOf(decollator, pos + 1);
		}
		if (key != null && prePos > 0) {
			map.put(key.trim(), str.substring(prePos + 1).trim());
		}
	}
}
