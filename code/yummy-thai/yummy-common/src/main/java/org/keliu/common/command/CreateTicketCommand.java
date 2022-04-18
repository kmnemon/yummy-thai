package org.keliu.common.command;

import io.eventuate.tram.commands.common.Command;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.keliu.common.domain.TicketDetails;

public class CreateTicketCommand implements Command {
    private long orderId;
    private TicketDetails ticketDetails;
    private long restaurantId;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public TicketDetails getTicketDetails() {
        return ticketDetails;
    }

    public void setTicketDetails(TicketDetails orderDetails) {
        this.ticketDetails = orderDetails;
    }

    private CreateTicketCommand() {

    }

    public CreateTicketCommand(long restaurantId, long orderId, TicketDetails ticketDetails) {
        this.restaurantId = restaurantId;
        this.orderId = orderId;
        this.ticketDetails = ticketDetails;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
