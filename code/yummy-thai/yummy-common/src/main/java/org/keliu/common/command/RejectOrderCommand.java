package org.keliu.common.command;

public class RejectOrderCommand extends OrderCommand {
    private RejectOrderCommand() {
    }
    public RejectOrderCommand(long orderId) {
        super(orderId);
    }
}

