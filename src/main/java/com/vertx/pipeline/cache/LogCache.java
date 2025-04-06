/**
 *
 */
package com.vertx.pipeline.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.vertx.core.json.JsonObject;


/** 
 * @author aalrbee 
 * */
public class LogCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogCache.class);
	private static final LogCache INSTANCE = new LogCache();
	private Cache<String, List<JsonObject>> logCache;
	private Cache<String, String> logIndex;
	private Map<String , String> logCriteria ;
	private LogCache() {
	}

	static {
		Duration duration = Duration.ofMinutes(15);
		INSTANCE.logCache = CacheBuilder.newBuilder().expireAfterWrite(duration).build();
		INSTANCE.logIndex = CacheBuilder.newBuilder().expireAfterWrite(duration).build();
		INSTANCE.logCriteria = new HashMap<>();
	}

	public static void addCriteria(String key, String messageContect) {
		INSTANCE.logCriteria.put(key, messageContect);
	}
	public static Map<String , String> getCriteria(){
		return INSTANCE.logCriteria;
	}
	public static void removeCriteria(String key) {
		INSTANCE.logCriteria.remove(key);
	}
	
	public static void addLogIndex(String index, String reqId) {
		INSTANCE.logIndex.put(index, reqId);
	}
	
	public static Map<String , String> getIndices(){
		return INSTANCE.logIndex.asMap();
	}
	
	public static String getLogByIndex(String index){
		return INSTANCE.logIndex.getIfPresent(index);
	}
	
	public static List<JsonObject> searchForLog(String index) {
		List<JsonObject> result = null;
		if(getLogByIndex(index) != null) {
			result = getLogByReqId(getLogByIndex(index));
		}
		return result;
	}
	
	public static void addLogToCache(String reqId, JsonObject log) {
		if(INSTANCE.logCache.getIfPresent(reqId) != null) {
			INSTANCE.logCache.getIfPresent(reqId).add(log);
		}else {
			List<JsonObject> logs = new ArrayList<>();
			logs.add(log);
			INSTANCE.logCache.put(reqId, logs);
		}
	}
	public static List<JsonObject> getLogByReqId(String reqId){
		return INSTANCE.logCache.getIfPresent(reqId);
	}
}
