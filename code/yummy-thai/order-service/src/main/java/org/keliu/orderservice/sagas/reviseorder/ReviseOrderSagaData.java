package org.keliu.orderservice.sagas.reviseorder;

import org.keliu.common.domain.Money;
import org.keliu.common.domain.order.OrderRevision;

public class ReviseOrderSagaData {
    private OrderRevision orderRevision;
    private Long orderId;
    private Long expectedVersion;
    private long restaurantId;
    private Money revisedOrderTotal;
    private long consumerId;

    private ReviseOrderSagaData() {
    }

    public ReviseOrderSagaData(long consumerId, Long orderId, Long expectedVersion, OrderRevision orderRevision) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.expectedVersion = expectedVersion;
        this.orderRevision = orderRevision;
    }

    public Long getExpectedVersion() {
        return expectedVersion;
    }

    public void setExpectedVersion(Long expectedVersion) {
        this.expectedVersion = expectedVersion;
    }

    public void setRevisedOrderTotal(Money revisedOrderTotal) {
        this.revisedOrderTotal = revisedOrderTotal;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }


    public OrderRevision getOrderRevision() {
        return orderRevision;
    }

    public void setOrderRevision(OrderRevision orderRevision) {
        this.orderRevision = orderRevision;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Money getRevisedOrderTotal() {
        return revisedOrderTotal;
    }

    public long getConsumerId() {
        return consumerId;
    }
}
