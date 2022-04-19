package org.keliu.orderservice.sagaparticipants.handler;

import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import org.keliu.common.command.order.ApproveOrderCommand;
import org.keliu.common.command.order.BeginCancelCommand;
import org.keliu.common.command.order.BeginReviseOrderCommand;
import org.keliu.common.command.order.ConfirmCancelOrderCommand;
import org.keliu.common.command.order.ConfirmReviseOrderCommand;
import org.keliu.common.command.order.RejectOrderCommand;
import org.keliu.common.command.order.UndoBeginCancelCommand;
import org.keliu.common.command.order.UndoBeginReviseOrderCommand;
import org.keliu.common.reply.BeginReviseOrderReply;
import org.keliu.common.domain.order.OrderRevision;
import org.keliu.common.exception.UnsupportedStateTransitionException;
import org.keliu.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;

public class OrderCommandHandlers {
    @Autowired
    private OrderService orderService;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder
                .fromChannel("orderService")
                .onMessage(ApproveOrderCommand.class, this::approveOrder)
                .onMessage(RejectOrderCommand.class, this::rejectOrder)

                .onMessage(BeginCancelCommand.class, this::beginCancel)
                .onMessage(UndoBeginCancelCommand.class, this::undoCancel)
                .onMessage(ConfirmCancelOrderCommand.class, this::confirmCancel)

                .onMessage(BeginReviseOrderCommand.class, this::beginReviseOrder)
                .onMessage(UndoBeginReviseOrderCommand.class, this::undoPendingRevision)
                .onMessage(ConfirmReviseOrderCommand.class, this::confirmRevision)
                .build();

    }

    public Message approveOrder(CommandMessage<ApproveOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.approveOrder(orderId);
        return withSuccess();
    }


    public Message rejectOrder(CommandMessage<RejectOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.rejectOrder(orderId);
        return withSuccess();
    }


    public Message beginCancel(CommandMessage<BeginCancelCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        try {
            orderService.beginCancel(orderId);
            return withSuccess();
        } catch (UnsupportedStateTransitionException e) {
            return withFailure();
        }
    }


    public Message undoCancel(CommandMessage<UndoBeginCancelCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.undoCancel(orderId);
        return withSuccess();
    }

    public Message confirmCancel(CommandMessage<ConfirmCancelOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.confirmCancelled(orderId);
        return withSuccess();
    }


    public Message beginReviseOrder(CommandMessage<BeginReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        OrderRevision revision = cm.getCommand().getRevision();
        try {
            return orderService.beginReviseOrder(orderId, revision).map(result -> withSuccess(new BeginReviseOrderReply(result.getChange().getNewOrderTotal()))).orElseGet(CommandHandlerReplyBuilder::withFailure);
        } catch (UnsupportedStateTransitionException e) {
            return withFailure();
        }
    }

    public Message undoPendingRevision(CommandMessage <UndoBeginReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        orderService.undoPendingRevision(orderId);
        return withSuccess();
    }

    public Message confirmRevision(CommandMessage<ConfirmReviseOrderCommand> cm) {
        long orderId = cm.getCommand().getOrderId();
        OrderRevision revision = cm.getCommand().getRevision();
        orderService.confirmRevision(orderId, revision);
        return withSuccess();
    }
}
