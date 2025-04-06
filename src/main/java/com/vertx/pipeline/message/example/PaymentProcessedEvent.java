/**
 * 
 */
package com.vertx.pipeline.message.example;

import com.vertx.pipeline.message.AbstractMessage;

/**
 * 
 */
//PaymentProcessedEvent.java 
public class PaymentProcessedEvent extends AbstractMessage {
	private final String orderId;
	private final boolean paymentSuccess;

	public PaymentProcessedEvent(AbstractMessage am, String orderId, boolean paymentSuccess) {
		super(am);
		this.orderId = orderId;
		this.paymentSuccess = paymentSuccess;
	}
	// Getters...

	public String getOrderId() {
		return orderId;
	}

	public boolean isPaymentSuccess() {
		return paymentSuccess;
	}

}