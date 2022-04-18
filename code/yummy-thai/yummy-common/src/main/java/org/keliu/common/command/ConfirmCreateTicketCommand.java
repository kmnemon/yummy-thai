package org.keliu.common.command;

import io.eventuate.tram.commands.common.Command;

public class ConfirmCreateTicketCommand implements Command {
    private long ticketId;

    private ConfirmCreateTicketCommand() {
    }

    public ConfirmCreateTicketCommand(Long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }
}
