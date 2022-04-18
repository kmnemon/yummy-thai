package org.keliu.orderservice.domain;

import org.keliu.common.domain.Money;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;

@Embeddable
public class OrderLineItems {
    @ElementCollection
    @CollectionTable(name = "order_line_items")
    private List<OrderLineItem> lineItems;

    public OrderLineItems(){}
    public OrderLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    OrderLineItem findOrderLineItem(String lineItemId) {
        return lineItems.stream().filter(li -> li.getMenuItemId().equals(lineItemId)).findFirst().get();
    }

    Money changeToOrderTotal(OrderRevision orderRevision) {
        return orderRevision
                .getRevisedOrderLineItems()
                .stream()
                .map(item -> {
                    OrderLineItem lineItem = findOrderLineItem(item.getMenuItemId());
                    return lineItem.deltaForChangedQuantity(item.getQuantity());
                })
                .reduce(Money.ZERO, Money::add);
    }

    Money orderTotal() {
        return lineItems.stream().map(OrderLineItem::getTotal).reduce(Money.ZERO, Money::add);
    }
}
