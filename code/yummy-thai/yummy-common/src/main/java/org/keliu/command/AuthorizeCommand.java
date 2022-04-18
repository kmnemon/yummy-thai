package org.keliu.command;

import io.eventuate.tram.commands.common.Command;
import org.keliu.domain.Money;

public class AuthorizeCommand implements Command {
    private long consumerId;
    private long orderId;
    private Money orderTotal;

    public AuthorizeCommand() {
    }

    public AuthorizeCommand(long consumerId, Long orderId, Money orderTotal) {
        this.consumerId = consumerId;
        this.orderId = orderId;
        this.orderTotal = orderTotal;
    }

    public long getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(long consumerId) {
        this.consumerId = consumerId;
    }

    public Money getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Money orderTotal) {
        this.orderTotal = orderTotal;
    }

    public long getOrderId() {

        return orderId;

    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
