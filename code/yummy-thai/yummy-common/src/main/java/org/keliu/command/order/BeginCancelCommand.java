package org.keliu.command.order;

public class BeginCancelCommand extends OrderCommand {

    private BeginCancelCommand() {
    }

    public BeginCancelCommand(long orderId) {
        super(orderId);
    }
}
