package org.keliu.common.domain.kitchen;

import io.eventuate.tram.commands.common.Command;
import org.keliu.common.domain.order.RevisedOrderLineItem;

import java.util.List;

public class BeginReviseTicketCommand implements Command {
    private long restaurantId;
    private Long orderId;
    private List<RevisedOrderLineItem> revisedOrderLineItems;

    private BeginReviseTicketCommand() {
    }

    public BeginReviseTicketCommand(long restaurantId, Long orderId, List<RevisedOrderLineItem> revisedOrderLineItems) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.revisedOrderLineItems = revisedOrderLineItems;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<RevisedOrderLineItem> getRevisedOrderLineItems() {
        return revisedOrderLineItems;
    }

    public void setRevisedOrderLineItems(List<RevisedOrderLineItem> revisedOrderLineItems) {
        this.revisedOrderLineItems = revisedOrderLineItems;
    }
}
