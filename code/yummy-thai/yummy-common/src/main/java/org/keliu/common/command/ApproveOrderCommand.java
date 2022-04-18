package org.keliu.common.command;

import io.eventuate.tram.commands.common.Command;

public class ApproveOrderCommand extends OrderCommand {
    private ApproveOrderCommand() {
    }

    public ApproveOrderCommand(long orderId) {
        super(orderId);
    }
}
