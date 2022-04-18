package org.keliu.command.order;

public class UndoBeginCancelCommand extends OrderCommand {
    public UndoBeginCancelCommand(long orderId) {
        super(orderId);
    }
}
