/**
 * 
 */
package com.vertx.pipeline.message.example;

import java.util.List;

import com.vertx.pipeline.message.AbstractMessage;

/**
 * 
 */
//OrderRequest.java
public class OrderRequest extends AbstractMessage {
 private final String orderId;
 private final List<String> items;
 private final double amount;

 public OrderRequest(AbstractMessage am, String orderId, List<String> items, double amount) {
	 super(am);
     this.orderId = orderId;
     this.items = List.copyOf(items);
     this.amount = amount;
 }
 // Getters...

public String getOrderId() {
	return orderId;
}

public List<String> getItems() {
	return items;
}

public double getAmount() {
	return amount;
}
 
}