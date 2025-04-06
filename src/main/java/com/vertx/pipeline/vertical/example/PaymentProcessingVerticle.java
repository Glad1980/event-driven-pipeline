/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.example.InventoryCheckedEvent;
import com.vertx.pipeline.message.example.PaymentProcessedEvent;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

/**
 * 
 */
public class PaymentProcessingVerticle extends GeneralVerticle {
    @Override public String getVerticalConsumer() { return "payment.process"; }
    
    @Override
    public void process(AbstractMessage message) {
        InventoryCheckedEvent event = (InventoryCheckedEvent) message;
        boolean paymentSuccess = DummyServices.processPayment(
            event.getOrderId(), 
            // Dummy amount calculation
            event.isInStock() ? 999.99 : 1001.00
        );
        
        PaymentProcessedEvent paymentEvent = new PaymentProcessedEvent(
            event,
            event.getOrderId(),
            paymentSuccess
        );
        
        send("order.confirm", paymentEvent);
    }
}