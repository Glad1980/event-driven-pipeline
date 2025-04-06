/**
 *
 */
package com.vertx.pipeline.vertical.core;

import static com.vertx.pipeline.util.Constant.ADDRESS_RESPONSE_VERTICLE;
import static com.vertx.pipeline.util.Constant.KEY_CORRELATION_ID;
import static com.vertx.pipeline.util.Constant.KEY_SYSTEM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.ResponseRoutingMessage;
import com.vertx.pipeline.util.HTTPRespCaching;
import com.vertx.pipeline.util.Util;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;

/**
 * @author aalrbee
 */
public class ResponseVerticle extends GeneralVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseVerticle.class);

	private void endHttpRequest(AbstractMessage message) {
		try {
			if (message != null) {
				ResponseRoutingMessage responseRoutingMessage = (ResponseRoutingMessage) message;
				JsonObject resMessage = responseRoutingMessage.getResponseBody();
				String correlationId = responseRoutingMessage.getCorrelationId();
				MDC.put(KEY_CORRELATION_ID, correlationId);
				HttpServerResponse httpServerResponse = HTTPRespCaching.retrieve(correlationId);
				if (httpServerResponse != null) {
					String endMessage = buildEndMessage(resMessage);
					httpServerResponse.setStatusCode(200).putHeader("Content-Type", "application/json; charset=utf-8")
							.end(endMessage);
					HTTPRespCaching.remove(correlationId);
				} else {
					LOGGER.error("null message arrived to {} ", this.getClass().getName());
				}
			} else {
				MDC.put(KEY_CORRELATION_ID, KEY_SYSTEM);
				LOGGER.error("messageReq is null!!");
			}
		} catch (Exception ex) {
			LOGGER.error("Exception in (endHttpRequest) Error={}", Util.getStackTrace(ex));
		}
	}

	private String buildEndMessage(JsonObject resMessage) {
		JsonObject endMsg = resMessage.copy();
		if (endMsg.containsKey(KEY_CORRELATION_ID)) {
			endMsg.remove(KEY_CORRELATION_ID);
			LOGGER.trace("Remove the key={} from  httpServerResponse message", KEY_CORRELATION_ID);
		}
		String msg = endMsg.toString();
		LOGGER.info("[[>>Response<<]]={}", msg);
		return msg;
	}

	@Override
	public String getVerticalConsumer() {
		return ADDRESS_RESPONSE_VERTICLE;
	}

	@Override
	public void process(AbstractMessage message) {
		endHttpRequest(message);

	}

}
