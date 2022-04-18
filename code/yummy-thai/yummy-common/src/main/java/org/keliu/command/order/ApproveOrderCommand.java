package org.keliu.command.order;

public class ApproveOrderCommand extends OrderCommand {
    private ApproveOrderCommand() {
    }

    public ApproveOrderCommand(long orderId) {
        super(orderId);
    }
}
