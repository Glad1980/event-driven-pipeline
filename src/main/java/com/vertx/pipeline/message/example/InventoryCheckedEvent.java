/**
 * 
 */
package com.vertx.pipeline.message.example;

import com.vertx.pipeline.message.AbstractMessage;

/**
 * 
 */
//InventoryCheckedEvent.java
public class InventoryCheckedEvent extends AbstractMessage {
	private final String orderId;
	private final boolean inStock;

	public InventoryCheckedEvent(AbstractMessage am, String orderId, boolean inStock) {
		super(am);
		this.orderId = orderId;
		this.inStock = inStock;
	}
	// Getters...

	public String getOrderId() {
		return orderId;
	}

	public boolean isInStock() {
		return inStock;
	}

}