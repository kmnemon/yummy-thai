package org.keliu.common.command.order;

public class UndoBeginReviseOrderCommand extends OrderCommand {

    protected UndoBeginReviseOrderCommand() {
    }

    public UndoBeginReviseOrderCommand(long orderId) {
        super(orderId);
    }
}

