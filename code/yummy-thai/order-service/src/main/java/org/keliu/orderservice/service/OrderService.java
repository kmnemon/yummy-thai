package org.keliu.orderservice.service;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.keliu.common.domain.delivery.DeliveryInformation;
import org.keliu.common.domain.order.OrderRevision;
import org.keliu.orderservice.exception.InvalidMenuItemIdException;
import org.keliu.orderservice.exception.OrderNotFoundException;
import org.keliu.orderservice.exception.RestaurantNotFoundException;
import org.keliu.orderservice.sagas.cancelorder.CancelOrderSaga;
import org.keliu.orderservice.sagas.cancelorder.CancelOrderSagaData;
import org.keliu.orderservice.sagas.createorder.CreateOrderSaga;
import org.keliu.orderservice.sagas.createorder.CreateOrderSagaData;
import org.keliu.orderservice.sagas.reviseorder.ReviseOrderSaga;
import org.keliu.orderservice.sagas.reviseorder.ReviseOrderSagaData;
import org.keliu.orderservice.domain.OrderDetails;
import org.keliu.orderservice.events.OrderDomainEvent;
import org.keliu.orderservice.events.OrderDomainEventPublisher;
import org.keliu.orderservice.domain.*;
import org.keliu.orderservice.repository.OrderRepository;
import org.keliu.orderservice.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
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

        CreateOrderSagaData data = new CreateOrderSagaData(order.getId(), orderDetails);
        sagaInstanceFactory.create(createOrderSaga, data);

        meterRegistry.ifPresent(mr-> mr.counter("placed_orders").increment());

        return order;
    }

    private List<OrderLineItem> makeOrderLineItems(List<MenuItemIdAndQuantity> lineItems, Restaurant restaurant){
        return lineItems.stream().map(li -> {
            MenuItem om = restaurant.findMenuItem(li.getMenuItemId()).orElseThrow(() -> new InvalidMenuItemIdException(li.getMenuItemId()));
            return new OrderLineItem(li.getMenuItemId(), om.getName(), om.getPrice(), li.getQuantity());
        }).collect(Collectors.toList());
    }

    public Optional<Order> confirmChangeLineItemQuantity(Long orderId, OrderRevision orderRevision) {
        return orderRepository.findById(orderId).map(order -> {
            List<OrderDomainEvent> events = order.confirmRevision(orderRevision);
            orderAggregateEventPublisher.publish(order, events);
            return order;
        });
    }

    public void noteReversingAuthorization(Long orderId) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    public Order cancel(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        CancelOrderSagaData sagaData = new CancelOrderSagaData(order.getConsumerId(), orderId, order.getOrderTotal());
        sagaInstanceFactory.create(cancelOrderSaga, sagaData);
        return order;
    }

    private Order updateOrder(long orderId, Function<Order, List<OrderDomainEvent>> updater) {
        return orderRepository.findById(orderId).map(order -> {
            orderAggregateEventPublisher.publish(order, updater.apply(order));
            return order;
        }).orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public void approveOrder(long orderId) {
        updateOrder(orderId, Order::noteApproved);
        meterRegistry.ifPresent(mr -> mr.counter("approved_orders").increment());
    }

    public void rejectOrder(long orderId) {
        updateOrder(orderId, Order::noteRejected);
        meterRegistry.ifPresent(mr -> mr.counter("rejected_orders").increment());
    }

    public void beginCancel(long orderId) {
        updateOrder(orderId, Order::cancel);
    }

    public void undoCancel(long orderId) {
        updateOrder(orderId, Order::undoPendingCancel);
    }

    public void confirmCancelled(long orderId) {
        updateOrder(orderId, Order::noteCancelled);
    }

    @Transactional
    public Order reviseOrder(long orderId, OrderRevision orderRevision) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        ReviseOrderSagaData sagaData = new ReviseOrderSagaData(order.getConsumerId(), orderId, null, orderRevision);
        sagaInstanceFactory.create(reviseOrderSaga, sagaData);
        return order;
    }

    public Optional<RevisedOrder> beginReviseOrder(long orderId, OrderRevision revision) {
        return orderRepository.findById(orderId).map(order -> {
            ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> result = order.revise(revision);
            orderAggregateEventPublisher.publish(order, result.events);
            return new RevisedOrder(order, result.result);
        });
    }

    public void undoPendingRevision(long orderId) {
        updateOrder(orderId, Order::rejectRevision);
    }

    public void confirmRevision(long orderId, OrderRevision revision) {
        updateOrder(orderId, order -> order.confirmRevision(revision));
    }

    public void createMenu(long id, String name, List<MenuItem> menuItems) {
        Restaurant restaurant = new Restaurant(id, name, menuItems);
        restaurantRepository.save(restaurant);
    }

    public void reviseMenu(long id, List<MenuItem> menuItems) {
        restaurantRepository.findById(id).map(restaurant -> {
            List<OrderDomainEvent> events = restaurant.reviseMenu(menuItems);
            return restaurant;
        }).orElseThrow(RuntimeException::new);
    }
}
