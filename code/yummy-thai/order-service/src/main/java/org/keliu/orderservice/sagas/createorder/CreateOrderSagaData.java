package org.keliu.orderservice.sagas.createorder;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.keliu.common.command.consumer.ValidateOrderByConsumerCommand;
import org.keliu.common.command.kitchen.CancelCreateTicketCommand;
import org.keliu.common.command.kitchen.ConfirmCreateTicketCommand;
import org.keliu.common.command.kitchen.CreateTicketCommand;
import org.keliu.common.command.order.ApproveOrderCommand;
import org.keliu.common.command.accounting.AuthorizeCommand;
import org.keliu.common.command.order.RejectOrderCommand;
import org.keliu.common.domain.kitchen.TicketDetails;
import org.keliu.common.domain.kitchen.TicketLineItem;
import org.keliu.common.reply.CreateTicketReply;
import org.keliu.orderservice.domain.OrderLineItem;
import org.keliu.orderservice.domain.OrderDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CreateOrderSagaData {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderSagaData.class);

    private long orderId;
    private OrderDetails orderDetails;
    private long ticketId;

    public CreateOrderSagaData(long orderId, OrderDetails orderDetails) {
        this.orderId = orderId;
        this.orderDetails = orderDetails;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    //command
    CreateTicketCommand makeCreateTicketCommand() {
        return new CreateTicketCommand(getOrderDetails().getRestaurantId(), getOrderId(), makeTicketDetails(getOrderDetails()));
    }

    private TicketDetails makeTicketDetails(OrderDetails orderDetails) {
        return new TicketDetails(makeTicketLineItems(orderDetails.getLineItems()));
    }

    private List<TicketLineItem> makeTicketLineItems(List<OrderLineItem> lineItems) {
        return lineItems.stream().map(this::makeTicketLineItem).collect(toList());
    }

    private TicketLineItem makeTicketLineItem(OrderLineItem orderLineItem) {
        return new TicketLineItem(orderLineItem.getMenuItemId(), orderLineItem.getName(), orderLineItem.getQuantity());
    }

    void handleCreateTicketReply(CreateTicketReply reply) {
        logger.debug("getTicketId {}", reply.getTicketId());
        setTicketId(reply.getTicketId());
    }

    CancelCreateTicketCommand makeCancelCreateTicketCommand() {
        return new CancelCreateTicketCommand(getOrderId());
    }

    RejectOrderCommand makeRejectOrderCommand() {
        return new RejectOrderCommand(getOrderId());
    }

    ValidateOrderByConsumerCommand makeValidateOrderByConsumerCommand() {
        ValidateOrderByConsumerCommand x = new ValidateOrderByConsumerCommand();
        x.setConsumerId(getOrderDetails().getConsumerId());
        x.setOrderId(getOrderId());
        x.setOrderTotal(getOrderDetails().getOrderTotal());
        return x;
    }

    AuthorizeCommand makeAuthorizeCommand() {
        return new AuthorizeCommand(getOrderDetails().getConsumerId(), getOrderId(), getOrderDetails().getOrderTotal());
    }

    ApproveOrderCommand makeApproveOrderCommand() {
        return new ApproveOrderCommand(getOrderId());
    }

    ConfirmCreateTicketCommand makeConfirmCreateTicketCommand() {
        return new ConfirmCreateTicketCommand(getTicketId());

    }


    //getter setter
    public long getOrderId(){ return orderId;}
    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setTicketId(long ticketId) {
        this.ticketId = ticketId;
    }

    public long getTicketId() {
        return ticketId;
    }

}
