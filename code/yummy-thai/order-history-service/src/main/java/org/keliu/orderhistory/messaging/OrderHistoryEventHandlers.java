package org.keliu.orderhistory.messaging;

import org.keliu.orderhistory.dao.OrderHistoryDao;
import org.keliu.orderhistory.domain.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

import java.util.Optional;

public class OrderHistoryEventHandlers {

    private OrderHistoryDao orderHistoryDao;

    public OrderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
        this.orderHistoryDao = orderHistoryDao;
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO - determine events

    private String orderId;
    private Order order;
    private Location location; //

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("net.chrisrichardson.ftgo.orderservice.domain.Order")
                .onEvent(OrderCreatedEvent.class, this::handleOrderCreated)
                .onEvent(OrderAuthorized.class, this::handleOrderAuthorized)
                .onEvent(OrderCancelled.class, this::handleOrderCancelled)
                .onEvent(OrderRejected.class, this::handleOrderRejected)
//            .onEvent(DeliveryPickedUp.class, this::handleDeliveryPickedUp)
                .build();
    }

    private Optional<SourceEvent> makeSourceEvent(DomainEventEnvelope<?> dee) {
        return Optional.of(new SourceEvent(dee.getAggregateType(),
                dee.getAggregateId(), dee.getEventId()));
    }

    public void handleOrderCreated(DomainEventEnvelope<OrderCreatedEvent> dee) {
        logger.debug("handleOrderCreated called {}", dee);
        boolean result = orderHistoryDao.addOrder(makeOrder(dee.getAggregateId(), dee.getEvent()), makeSourceEvent(dee));
        logger.debug("handleOrderCreated result {} {}", dee, result);
    }

    public void handleOrderAuthorized(DomainEventEnvelope<OrderAuthorized> dee) {
        logger.debug("handleOrderAuthorized called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.APPROVED, makeSourceEvent(dee));
        logger.debug("handleOrderAuthorized result {} {}", dee, result);
    }

    public void handleOrderCancelled(DomainEventEnvelope<OrderCancelled> dee) {
        logger.debug("handleOrderCancelled called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.CANCELLED, makeSourceEvent(dee));
        logger.debug("handleOrderCancelled result {} {}", dee, result);
    }

    public void handleOrderRejected(DomainEventEnvelope<OrderRejected> dee) {
        logger.debug("handleOrderRejected called {}", dee);
        boolean result = orderHistoryDao.updateOrderState(dee.getAggregateId(), OrderState.REJECTED, makeSourceEvent(dee));
        logger.debug("handleOrderRejected result {} {}", dee, result);
    }

    private Order makeOrder(String orderId, OrderCreatedEvent event) {
        return new Order(orderId,
                Long.toString(event.getOrderDetails().getConsumerId()),
                OrderState.APPROVAL_PENDING,
                event.getOrderDetails().getLineItems(),
                event.getOrderDetails().getOrderTotal(),
                event.getOrderDetails().getRestaurantId(),
                event.getRestaurantName());
    }

    public void handleDeliveryPickedUp(DomainEventEnvelope<DeliveryPickedUp>
                                               dee) {
        orderHistoryDao.notePickedUp(dee.getEvent().getOrderId(),
                makeSourceEvent(dee));
    }
