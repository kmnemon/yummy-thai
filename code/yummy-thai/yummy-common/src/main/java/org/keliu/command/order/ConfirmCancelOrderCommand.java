package org.keliu.command.order;

public class ConfirmCancelOrderCommand extends OrderCommand {
    private ConfirmCancelOrderCommand() {
    }

    public ConfirmCancelOrderCommand(long orderId) {
        super(orderId);
    }
}
