package org.keliu.orderhistory.dao;

import org.keliu.orderhistory.domain.Location;
import org.keliu.orderhistory.domain.OrderHistory;
import org.springframework.core.annotation.Order;

import java.util.Optional;

public interface OrderHistoryDao {

    boolean addOrder(Order order, Optional<SourceEvent> eventSource);

    OrderHistory findOrderHistory(String consumerId, OrderHistoryFilter filter);

    boolean updateOrderState(String orderId, OrderState newState, Optional<SourceEvent> eventSource);

    void noteTicketPreparationStarted(String orderId);

    void noteTicketPreparationCompleted(String orderId);

    void notePickedUp(String orderId, Optional<SourceEvent> eventSource);

    void updateLocation(String orderId, Location location);

    void noteDelivered(String orderId);

    Optional<Order> findOrder(String orderId);

}
