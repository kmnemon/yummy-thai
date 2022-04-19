package org.keliu.orderservice.sagas.reviseorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.keliu.common.channel.AccountingServiceChannels;
import org.keliu.common.channel.KitchenServiceChannels;
import org.keliu.common.channel.OrderServiceChannels;
import org.keliu.common.command.accounting.ReviseAuthorization;
import org.keliu.common.command.kitchen.UndoBeginReviseTicketCommand;
import org.keliu.common.command.order.BeginReviseOrderCommand;
import org.keliu.common.command.order.ConfirmReviseOrderCommand;
import org.keliu.common.command.order.UndoBeginReviseOrderCommand;
import org.keliu.common.domain.kitchen.BeginReviseTicketCommand;
import org.keliu.common.domain.kitchen.ConfirmReviseTicketCommand;
import org.keliu.common.reply.BeginReviseOrderReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class ReviseOrderSaga implements SimpleSaga<ReviseOrderSagaData> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private SagaDefinition<ReviseOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                .invokeParticipant(this::beginReviseOrder)
                .onReply(BeginReviseOrderReply.class, this::handleBeginReviseOrderReply)
                .withCompensation(this::undoBeginReviseOrder)
                .step()
                .invokeParticipant(this::beginReviseTicket)
                .withCompensation(this::undoBeginReviseTicket)
                .step()
                .invokeParticipant(this::reviseAuthorization)
                .step()
                .invokeParticipant(this::confirmTicketRevision)
                .step()
                .invokeParticipant(this::confirmOrderRevision)
                .build();
    }

    private void handleBeginReviseOrderReply(ReviseOrderSagaData data, BeginReviseOrderReply reply) {
        logger.info("ƒ order total: {}", reply.getRevisedOrderTotal());
        data.setRevisedOrderTotal(reply.getRevisedOrderTotal());
    }

    @Override
    public SagaDefinition<ReviseOrderSagaData> getSagaDefinition() {
        return sagaDefinition;
    }

    private CommandWithDestination confirmOrderRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination confirmTicketRevision(ReviseOrderSagaData data) {
        return send(new ConfirmReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedOrderLineItems()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination reviseAuthorization(ReviseOrderSagaData data) {
        return send(new ReviseAuthorization(data.getConsumerId(), data.getOrderId(), data.getRevisedOrderTotal()))
                .to(AccountingServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination undoBeginReviseTicket(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination beginReviseTicket(ReviseOrderSagaData data) {
        return send(new BeginReviseTicketCommand(data.getRestaurantId(), data.getOrderId(), data.getOrderRevision().getRevisedOrderLineItems()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination undoBeginReviseOrder(ReviseOrderSagaData data) {
        return send(new UndoBeginReviseOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination beginReviseOrder(ReviseOrderSagaData data) {
        return send(new BeginReviseOrderCommand(data.getOrderId(), data.getOrderRevision()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();

    }
}
