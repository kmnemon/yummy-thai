package org.keliu.command.order;

import org.keliu.domain.order.OrderRevision;

public class ConfirmReviseOrderCommand extends OrderCommand {

    private ConfirmReviseOrderCommand() {
    }

    public ConfirmReviseOrderCommand(long orderId, OrderRevision revision) {
        super(orderId);
        this.revision = revision;
    }

    private OrderRevision revision;

    public OrderRevision getRevision() {
        return revision;
    }
}