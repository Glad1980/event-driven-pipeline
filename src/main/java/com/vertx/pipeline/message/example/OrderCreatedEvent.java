/**
 * 
 */
package com.vertx.pipeline.message.example;

import java.util.List;

import com.vertx.pipeline.message.AbstractMessage;

/**
 * 
 */
//OrderCreatedEvent.java
public class OrderCreatedEvent extends AbstractMessage {
	private final String orderId;
	private final List<String> items;
	private final boolean isValid;

	public OrderCreatedEvent(AbstractMessage am, String orderId, List<String> items, boolean isValid) {
		super(am);
		this.orderId = orderId;
		this.items = items;
		this.isValid = isValid;
	}
	// Getters...

	public String getOrderId() {
		return orderId;
	}

	public List<String> getItems() {
		return items;
	}

	public boolean isValid() {
		return isValid;
	}

}