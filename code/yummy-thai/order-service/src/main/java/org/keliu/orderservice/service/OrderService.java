package org.keliu.orderservice.service;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.keliu.orderservice.events.OrderDetails;
import org.keliu.orderservice.events.OrderDomainEvent;
import org.keliu.orderservice.events.OrderDomainEventPublisher;
import org.keliu.orderservice.exception.InvalidMenuItemIdException;
import org.keliu.orderservice.exception.OrderNotFoundException;
import org.keliu.orderservice.exception.RestaurantNotFoundException;
import org.keliu.orderservice.domain.*;
import org.keliu.orderservice.repository.OrderRepository;
import org.keliu.orderservice.repository.RestaurantRepository;
import org.keliu.orderservice.sagas.cancelorder.CancelOrderSaga;
import org.keliu.orderservice.sagas.createorder.CreateOrderSaga;
import org.keliu.orderservice.sagas.createorder.CreateOrderSagaState;
import org.keliu.orderservice.sagas.reviseorder.ReviseOrderSaga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SagaInstanceFactory sagaInstanceFactory;

    @Autowired
    private CreateOrderSaga createOrderSaga;
    @Autowired
    private CancelOrderSaga cancelOrderSaga;
    @Autowired
    private ReviseOrderSaga reviseOrderSaga;

    @Autowired
    private OrderDomainEventPublisher orderAggregateEventPublisher;
    @Autowired
    private Optional<MeterRegistry> meterRegistry;

    @Transactional
    public Order createOrder(long consumerId, long restaurantId, DeliveryInformation deliveryInformation,
                             List<MenuItemIdAndQuantity> lineItems){
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

        ResultWithDomainEvents<Order, OrderDomainEvent> orderAndEvents =
                Order.createOrder(consumerId, restaurant, deliveryInformation, orderLineItems);

        Order order = orderAndEvents.result;
        orderRepository.save(order);

        orderAggregateEventPublisher.publish(order, orderAndEvents.events);

        OrderDetails orderDetails = new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal());

        CreateOrderSagaState data = new CreateOrderSagaState(order.getId(), orderDetails);
        sagaInstanceFactory.create(createOrderSaga, data);



    }

    private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant){
        return lineItems.stream().map(li -> {
            MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
            return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
        }).collect(Collectors.toList());
    }

    @Transactional
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
//        CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
//        sagaInstanceFactory.create(cancelOrderSaga, sagaData);
        return order;
    }
}
