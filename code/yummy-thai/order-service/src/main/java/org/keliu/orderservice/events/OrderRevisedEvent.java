package org.keliu.orderservice.events;

import org.keliu.domain.Money;
import org.keliu.orderservice.domain.OrderRevision;

public class OrderRevisedEvent implements OrderDomainEvent{
    private final OrderRevision orderRevision;
    private final Money currentOrderTotal;
    private final Money newOrderTotal;

    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }

    public OrderRevisedEvent(OrderRevision orderRevision, Money currentOrderTotal, Money newOrderTotal) {
        this.orderRevision = orderRevision;
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;


    }
}
