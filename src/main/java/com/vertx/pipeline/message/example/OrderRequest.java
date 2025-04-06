/**
 * 
 */
package com.vertx.pipeline.message.example;

import java.util.List;

/**
 * 
 */
//OrderRequest.java
public class OrderRequest {
 private final String orderId;
 private final List<String> items;
 private final double amount;

 public OrderRequest( String orderId, List<String> items, double amount) {
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