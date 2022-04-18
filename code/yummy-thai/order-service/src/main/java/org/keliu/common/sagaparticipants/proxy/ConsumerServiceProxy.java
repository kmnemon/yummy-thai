package org.keliu.common.sagaparticipants.proxy;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.keliu.common.command.ValidateOrderByConsumerCommand;
import org.keliu.common.channel.ConsumerServiceChannels;

public class ConsumerServiceProxy {
    public final CommandEndpoint<ValidateOrderByConsumerCommand> validateOrder= CommandEndpointBuilder
            .forCommand(ValidateOrderByConsumerCommand.class)
            .withChannel(ConsumerServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
