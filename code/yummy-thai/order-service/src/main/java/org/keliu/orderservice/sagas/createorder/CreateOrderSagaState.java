package org.keliu.orderservice.sagas.createorder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.keliu.orderservice.events.OrderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderSagaState {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderSagaState.class);

    private Long orderId;
    private OrderDetails orderDetails;
    private Long ticketId;

    public CreateOrderSagaState(Long orderId, OrderDetails orderDetails) {
        this.orderId = orderId;
        this.orderDetails = orderDetails;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }



    //getter setter
    public Long getOrderId(){ return orderId;}
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

}
