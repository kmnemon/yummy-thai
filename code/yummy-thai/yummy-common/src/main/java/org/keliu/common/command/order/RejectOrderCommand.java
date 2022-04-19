package org.keliu.common.command.order;

public class RejectOrderCommand extends OrderCommand {
    private RejectOrderCommand() {
    }
    public RejectOrderCommand(long orderId) {
        super(orderId);
    }
}

