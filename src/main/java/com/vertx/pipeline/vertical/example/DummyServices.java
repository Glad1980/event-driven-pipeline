/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import java.util.List;
import java.util.Random;

import com.vertx.pipeline.message.example.OrderRequest;

/**
 * 
 */
public class DummyServices {
	public static boolean validateOrder(OrderRequest order) {
		return !order.getItems().isEmpty() && order.getAmount() > 0;
	}

	public static boolean checkInventory(List<String> items) {
		return new Random().nextBoolean(); // Random stock status
	}

	public static boolean processPayment(String orderId, double amount) {
		return amount < 1000; // Approve payments under $1000
	}

	public static String prepareConfirmation(String orderId, String correlationId) {
		return String.format("[CID: %s] Confirmation sent for order %s%n", correlationId, orderId);
	}

}
