/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import java.util.ArrayList;
import java.util.List;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.RequestRoutingMessage;
import com.vertx.pipeline.message.example.OrderCreatedEvent;
import com.vertx.pipeline.message.example.OrderRequest;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

import io.vertx.core.json.JsonArray;
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
		JsonArray jsonArray = req.getJsonArray("items");
		List<String> items = new ArrayList<String>();
		for (int i = 0; i < jsonArray.size(); i++) {
			items.add(jsonArray.getString(i));
			OrderRequest or = new OrderRequest(req.getString("orderId"), items, req.getDouble("amount"));
			boolean isValid = DummyServices.validateOrder(or);

			OrderCreatedEvent event = new OrderCreatedEvent(request, or.getOrderId(), or.getItems(), isValid);

			send("inventory.check", event);
		}
	}
}