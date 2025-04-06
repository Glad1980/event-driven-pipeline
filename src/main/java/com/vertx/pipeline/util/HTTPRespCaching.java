/**
 *
 */
package com.vertx.pipeline.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vertx.core.http.HttpServerResponse;

import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 *  Used to cache the Http Server Response
 * @author aalrbee
 * */
public class HTTPRespCaching {

	private static final Logger LOGGER = LoggerFactory.getLogger(HTTPRespCaching.class);
	private static Cache<String, HttpServerResponse> httpRespCache;

	private HTTPRespCaching() {
	}
	/**
	 * @param correlationId The original correlationId witch cached in the cluster cache.
	 * @return HttpServerResponse of The original request
	 */
	public static HttpServerResponse retrieve(String correlationId) {
		MDC.put(KEY_CORRELATION_ID, correlationId);
		if (httpRespCache == null) {
			return null;
		}
		HttpServerResponse httpRespCacheInstance = httpRespCache.getIfPresent(correlationId);
		httpRespCache.invalidate(correlationId);
		LOGGER.info("Retrieve HTTP Response from cache");
		return httpRespCacheInstance;
	}

	/**
	 * @param correlationId    The original correlationId witch cached in the cluster cache.
	 * @param response    HttpServerResponse of The original request
	 */
	public static void add(String correlationId, HttpServerResponse response) {
		MDC.put(KEY_CORRELATION_ID, correlationId);
		if (httpRespCache == null) {
			httpRespCache = CacheBuilder.newBuilder().build();
		}
		httpRespCache.put(correlationId, response);
		LOGGER.info("Add HTTP Response to cache");
	}

	public static void remove(String correlationId) {
		httpRespCache.invalidate(correlationId);
	}
}
