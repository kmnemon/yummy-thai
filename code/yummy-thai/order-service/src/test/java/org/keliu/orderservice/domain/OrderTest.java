package org.keliu.orderservice.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.events.publisher.ResultWithEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keliu.common.domain.Address;
import org.keliu.common.domain.delivery.DeliveryInformation;
import org.keliu.orderservice.events.OrderCreatedEvent;
import org.keliu.orderservice.events.OrderDomainEvent;
import org.keliu.orderservice.helper.YummyFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    private ResultWithDomainEvents<Order, OrderDomainEvent> createResult;
    private Order order;

    @BeforeEach
    public void setUp() throws Exception{
        long consumerId = 1;
        Restaurant restaurant = YummyFactory.createRestaurant();
        DeliveryInformation deliveryInformation = YummyFactory.createDeliveryInformation();
        List<OrderLineItem> orderLineItems = YummyFactory.createOrderLineItem();

        createResult = Order.createOrder(consumerId, restaurant, deliveryInformation, orderLineItems);
        order = createResult.result;
    }

    @Test
    public void createOrderTest(){
        long consumerId = 1;
        Restaurant restaurant = YummyFactory.createRestaurant();
        DeliveryInformation deliveryInformation = YummyFactory.createDeliveryInformation();
        List<OrderLineItem> orderLineItems = YummyFactory.createOrderLineItem();

        Order o = new Order(consumerId, restaurant.getId(), deliveryInformation, orderLineItems);
        OrderDomainEvent oDomainEvent =  new OrderCreatedEvent(new OrderDetails(consumerId, restaurant.getId(), orderLineItems, o.getOrderTotal()),
                deliveryInformation.getDeliveryAddress(),
                restaurant.getName()
        );

        assertEquals(o, order);
        assertEquals(1, createResult.events.size());
        assertEquals(oDomainEvent, createResult.events.get(0));

    }
}
