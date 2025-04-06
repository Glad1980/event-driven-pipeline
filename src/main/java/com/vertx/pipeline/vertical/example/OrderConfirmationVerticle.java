/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import static com.vertx.pipeline.util.Constant.ADDRESS_RESPONSE_VERTICLE;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.ResponseRoutingMessage;
import com.vertx.pipeline.message.example.PaymentProcessedEvent;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

import io.vertx.core.json.JsonObject;

/**
 * 
 */
public class OrderConfirmationVerticle extends GeneralVerticle {
    @Override public String getVerticalConsumer() { return "order.confirm"; }
    
    @Override
    public void process(AbstractMessage message) {
        PaymentProcessedEvent event = (PaymentProcessedEvent) message;
        String confirmation = DummyServices.prepareConfirmation(
            event.getOrderId(),
            event.getCorrelationId()
        );
        JsonObject res = new JsonObject();
		res.put("status", "Success");
		res.put("correlationId", event.getCorrelationId());
        ResponseRoutingMessage resrMessage = new ResponseRoutingMessage(event, res, confirmation);
        send(ADDRESS_RESPONSE_VERTICLE, resrMessage);
    }
}