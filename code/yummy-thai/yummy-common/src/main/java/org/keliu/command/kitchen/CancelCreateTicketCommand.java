package org.keliu.command.kitchen;

import io.eventuate.tram.commands.common.Command;

public class CancelCreateTicketCommand implements Command {
    private long ticketId;

    private CancelCreateTicketCommand() {
    }

    public CancelCreateTicketCommand(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }
}
