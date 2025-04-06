/**
 * 
 */
package com.vertx.pipeline.vertical.example;

import com.vertx.pipeline.message.AbstractMessage;
import com.vertx.pipeline.message.example.InventoryCheckedEvent;
import com.vertx.pipeline.message.example.OrderCreatedEvent;
import com.vertx.pipeline.vertical.core.GeneralVerticle;

/**
 * 
 */
public class InventoryCheckVerticle extends GeneralVerticle {
    @Override public String getVerticalConsumer() { return "inventory.check"; }
    
    @Override
    public void process(AbstractMessage message) {
        OrderCreatedEvent event = (OrderCreatedEvent) message;
        boolean inStock = DummyServices.checkInventory(event.getItems());
        
        InventoryCheckedEvent checkedEvent = new InventoryCheckedEvent(
            event,
            event.getOrderId(),
            inStock
        );
        
        send("payment.process", checkedEvent);
    }
}