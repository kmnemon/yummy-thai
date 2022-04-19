package org.keliu.orderservice.controller;

import org.keliu.common.domain.order.RevisedOrderLineItem;

import java.util.List;

public class ReviseOrderRequest {
    private List<RevisedOrderLineItem> revisedOrderLineItems;

    private ReviseOrderRequest() {
    }

    public ReviseOrderRequest(List<RevisedOrderLineItem> revisedOrderLineItems) {
        this.revisedOrderLineItems = revisedOrderLineItems;
    }

    public List<RevisedOrderLineItem> getRevisedOrderLineItems() {
        return revisedOrderLineItems;
    }

    public void setRevisedOrderLineItems(List<RevisedOrderLineItem> revisedOrderLineItems) {
        this.revisedOrderLineItems = revisedOrderLineItems;
    }
}
