package org.keliu.command.order;

public class RejectOrderCommand extends OrderCommand {
    private RejectOrderCommand() {
    }
    public RejectOrderCommand(long orderId) {
        super(orderId);
    }
}

