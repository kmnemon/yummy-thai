package org.keliu.orderservice.sagas.cancelorder;

import io.eventuate.tram.commands.consumer.CommandWithDestination;
import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.keliu.common.channel.AccountingServiceChannels;
import org.keliu.common.channel.KitchenServiceChannels;
import org.keliu.common.channel.OrderServiceChannels;
import org.keliu.common.command.accounting.ReverseAuthorizationCommand;
import org.keliu.common.command.kitchen.BeginCancelTicketCommand;
import org.keliu.common.command.kitchen.ConfirmCancelTicketCommand;
import org.keliu.common.command.kitchen.UndoBeginCancelTicketCommand;
import org.keliu.common.command.order.BeginCancelCommand;
import org.keliu.common.command.order.ConfirmCancelOrderCommand;
import org.keliu.common.command.order.UndoBeginCancelCommand;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static io.eventuate.tram.commands.consumer.CommandWithDestinationBuilder.send;

public class CancelOrderSaga implements SimpleSaga<CancelOrderSagaData> {
    private SagaDefinition<CancelOrderSagaData> sagaDefinition;

    @PostConstruct
    public void initializeSagaDefinition() {
        sagaDefinition = step()
                .invokeParticipant(this::beginCancel)
                .withCompensation(this::undoBeginCancel)
                .step()
                .invokeParticipant(this::beginCancelTicket)
                .withCompensation(this::undoBeginCancelTicket)
                .step()
                .invokeParticipant(this::reverseAuthorization)
                .step()
                .invokeParticipant(this::confirmTicketCancel)
                .step()
                .invokeParticipant(this::confirmOrderCancel)
                .build();

    }

    private CommandWithDestination confirmOrderCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelOrderCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination confirmTicketCancel(CancelOrderSagaData data) {
        return send(new ConfirmCancelTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination reverseAuthorization(CancelOrderSagaData data) {
        return send(new ReverseAuthorizationCommand(data.getConsumerId(), data.getOrderId(), data.getOrderTotal()))
                .to(AccountingServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination undoBeginCancelTicket(CancelOrderSagaData data) {
        return send(new UndoBeginCancelTicketCommand(data.getRestaurantId(), data.getOrderId()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination beginCancelTicket(CancelOrderSagaData data) {
        return send(new BeginCancelTicketCommand(data.getRestaurantId(), (long) data.getOrderId()))
                .to(KitchenServiceChannels.COMMAND_CHANNEL)
                .build();

    }

    private CommandWithDestination undoBeginCancel(CancelOrderSagaData data) {
        return send(new UndoBeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }

    private CommandWithDestination beginCancel(CancelOrderSagaData data) {
        return send(new BeginCancelCommand(data.getOrderId()))
                .to(OrderServiceChannels.COMMAND_CHANNEL)
                .build();
    }


    @Override
    public SagaDefinition<CancelOrderSagaData> getSagaDefinition() {
        Assert.notNull(sagaDefinition,"[Assertion failed] - this argument is required; it must not be null");
        return sagaDefinition;
    }
}