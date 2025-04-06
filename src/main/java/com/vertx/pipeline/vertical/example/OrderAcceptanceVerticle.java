/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.RequestRoutingMessage;
import com.vertx.pipeline.message.example.OrderCreatedEvent;
import com.vertx.pipeline.message.example.OrderRequest;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

import io.vertx.core.json.JsonObject;

/**
 * 
 */
//Verticles (Updated with Dummy Implementations)
public class OrderAcceptanceVerticle extends GeneralVerticle {
	@Override
	public String getVerticalConsumer() {
		return "order.accept";
	}

	@Override
	public void process(AbstractMessage message) {
		RequestRoutingMessage request = (RequestRoutingMessage) message;
		JsonObject req = request.getRequestBody();
		
		boolean isValid = DummyServices.validateOrder(request);

		OrderCreatedEvent event = new OrderCreatedEvent(request, request.getOrderId(), request.getItems(), isValid);

		send("inventory.check", event);
	} 
}