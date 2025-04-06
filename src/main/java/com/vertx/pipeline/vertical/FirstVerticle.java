package com.vertx.pipeline.vertical;

import static com.vertx.pipeline.util.Constant.ADDRESS_FIRST_VERTICLE;
import static com.vertx.pipeline.util.Constant.ADDRESS_RESPONSE_VERTICLE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vertx.pipeline.cache.LogCache;
import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.RequestRoutingMessage;
import com.vertx.pipeline.message.ResponseRoutingMessage;
import com.vertx.pipeline.util.Util;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

import io.vertx.core.json.JsonObject;

/**
 *
 * @author aalrbee
 */
public class FirstVerticle extends GeneralVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirstVerticle.class);

	@Override
	public void process(AbstractMessage message) {
		try {
			if (message != null) {
				RequestRoutingMessage rrm = (RequestRoutingMessage) message;
				JsonObject req = rrm.getRequestBody();
				LOGGER.info("correlation ID [{}] , correlation is = {}", rrm.getCorrelationId(),req);
				
				
				String key = req.getString("key");
				String messageContains = req.getString("messageContains");
				if (messageContains != null) {
					LogCache.addCriteria(key, messageContains);
				}
				JsonObject res = new JsonObject();
				res.put("status", "Success");
				res.put("correlationId", rrm.getCorrelationId());
				ResponseRoutingMessage resrMessage = new ResponseRoutingMessage(rrm, res, "Success");
            send(ADDRESS_RESPONSE_VERTICLE, resrMessage);
			}
		} catch (Exception e) {
			Util.logEx(e, this.getClass().getSimpleName());
			ResponseRoutingMessage resrMessage = new ResponseRoutingMessage(message,  new JsonObject().put("status", "Error"), "Error");
            endRequest(message.getCorrelationId(),resrMessage);
		}
	}

	@Override
	public String getVerticalConsumer() {
		return ADDRESS_FIRST_VERTICLE;
	}
}
