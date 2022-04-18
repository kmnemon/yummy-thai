package org.keliu.orderservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.keliu.common.domain.Money;
import org.keliu.orderservice.events.*;
import org.keliu.orderservice.exception.UnsupportedStateTransitionException;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.keliu.orderservice.domain.OrderState.*;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {
    public static ResultWithDomainEvents<Order, OrderDomainEvent> createOrder(long consumerId, Restaurant restaurant, DeliveryInformation deliveryInformation, List<OrderLineItem> orderLineItems){
        Order order = new Order(consumerId, restaurant.getId(), deliveryInformation, orderLineItems);
        List<OrderCreatedEvent> events = Collections.singletonList(new OrderCreatedEvent(new OrderDetails(consumerId, restaurant.getId(), orderLineItems, order.getOrderTotal()),
                deliveryInformation.getDeliveryAddress(),
                restaurant.getName()
        ));

        return new ResultWithDomainEvents<>(order, events);
    }

    @Id
    @GeneratedValue
    private long id;

    @Version
    private long version;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private long consumerId;
    private long restaurantId;

    @Embedded
    private OrderLineItems orderLineItems;

    @Embedded
    private DeliveryInformation deliveryInformation;

    @Embedded
    private PaymentInformation paymentInformation;

    @Embedded
    private Money orderMinimum = new Money(Integer.MIN_VALUE);

    public Order(){}

    public Order(long consumerId, long restaurantId, DeliveryInformation deliveryInformation, List<OrderLineItem> orderLineItems) {
        this.consumerId = consumerId;
        this.restaurantId = restaurantId;
        this.deliveryInformation = deliveryInformation;
        this.orderLineItems = new OrderLineItems(orderLineItems);
        this.state = APPROVAL_PENDING;
    }

    //order operate
    public List<OrderDomainEvent> cancel() {
        switch (state) {
            case APPROVED:
                this.state = OrderState.CANCEL_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<OrderDomainEvent> undoPendingCancel() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = APPROVED;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<OrderDomainEvent> noteCancelled() {
        switch (state) {
            case CANCEL_PENDING:
                this.state = OrderState.CANCELLED;
                return singletonList(new OrderCancelled());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<OrderDomainEvent> noteApproved() {
        switch (state) {
            case APPROVAL_PENDING:
                this.state = APPROVED;
                return singletonList(new OrderAuthorized());
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<OrderDomainEvent> noteRejected() {
        switch (state) {
            case APPROVAL_PENDING:
                this.state = REJECTED;
                return singletonList(new OrderRejected());

            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }


    //getter setter
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    public Money getOrderTotal() {
        return orderLineItems.orderTotal();
    }
    public OrderState getState() {
        return state;
    }


}
