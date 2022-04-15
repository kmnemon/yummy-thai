package org.keliu.orderservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import org.keliu.orderservice.common.Money;
import org.keliu.orderservice.events.OrderCreatedEvent;
import org.keliu.orderservice.events.OrderDetails;
import org.keliu.orderservice.events.OrderDomainEvent;
import org.keliu.orderservice.exception.UnsupportedStateTransitionException;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.keliu.orderservice.domain.OrderState.APPROVAL_PENDING;

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
public class Order {
    public static ResultWithDomainEvents<Order, OrderDomainEvent> createOrder(long consumerId, Restaurant restaurant, DeliveryInformation deliveryInformation, List<OrderLineItem> orderLineItems){
        Order order = new Order(consumerId, restaurant.getId(), deliveryInformation, orderLineItems);
        List<OrderDomainEvent> events = Collections.singletonList(new OrderCreatedEvent(new OrderDetails(consumerId, restaurant.getId(), orderLineItems, order.getOrderTotal()),
                deliveryInformation.getDeliveryAddress(),
                restaurant.getName()
        ));

        return new ResultWithDomainEvents<>(order, events);
    }

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private Long version;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    private Long consumerId;
    private Long restaurantId;

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

    public List<OrderDomainEvent> cancel() {
        switch (state) {
            case APPROVED:
                this.state = OrderState.CANCEL_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }


    //getter setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public DeliveryInformation getDeliveryInformation() {
        return deliveryInformation;
    }

    public Money getOrderTotal() {
        return orderLineItems.orderTotal();
    }


}
