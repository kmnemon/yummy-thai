package org.keliu.command.order;

public class UndoBeginReviseOrderCommand extends OrderCommand {

    protected UndoBeginReviseOrderCommand() {
    }

    public UndoBeginReviseOrderCommand(long orderId) {
        super(orderId);
    }
}

