package org.keliu.common.command.order;

import io.eventuate.tram.commands.common.Command;

public class OrderCommand implements Command {
    private long orderId;
    protected OrderCommand(){}

    protected OrderCommand(long orderId) {
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
}
