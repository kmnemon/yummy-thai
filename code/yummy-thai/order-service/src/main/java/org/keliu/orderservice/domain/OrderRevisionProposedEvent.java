package org.keliu.orderservice.domain;

import org.keliu.common.domain.Money;
import org.keliu.common.domain.order.OrderRevision;
import org.keliu.orderservice.events.OrderDomainEvent;

public class OrderRevisionProposedEvent implements OrderDomainEvent {
    private final OrderRevision orderRevision;
    private final Money currentOrderTotal;
    private final Money newOrderTotal;

    public OrderRevisionProposedEvent(OrderRevision orderRevision, Money currentOrderTotal, Money newOrderTotal) {
        this.orderRevision = orderRevision;
        this.currentOrderTotal = currentOrderTotal;
        this.newOrderTotal = newOrderTotal;
    }

    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public Money getCurrentOrderTotal() {
        return currentOrderTotal;
    }

    public Money getNewOrderTotal() {
        return newOrderTotal;
    }
}
