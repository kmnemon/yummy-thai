package org.keliu.orderservice.sagaparticipants.proxy;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.keliu.common.channel.KitchenServiceChannels;
import org.keliu.common.command.kitchen.CancelCreateTicketCommand;
import org.keliu.common.command.kitchen.ConfirmCreateTicketCommand;
import org.keliu.common.command.kitchen.CreateTicketCommand;
import org.keliu.common.reply.CreateTicketReply;

public class KitchenServiceProxy {
    public final CommandEndpoint<CreateTicketCommand> create = CommandEndpointBuilder
            .forCommand(CreateTicketCommand.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(CreateTicketReply.class)
            .build();

    public final CommandEndpoint<ConfirmCreateTicketCommand> confirmCreate = CommandEndpointBuilder
            .forCommand(ConfirmCreateTicketCommand.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
    public final CommandEndpoint<CancelCreateTicketCommand> cancel = CommandEndpointBuilder
            .forCommand(CancelCreateTicketCommand.class)
            .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
