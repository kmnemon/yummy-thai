package org.keliu.orderservice.sagaparticipants.proxy;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.keliu.channel.ConsumerServiceChannels;
import org.keliu.command.consumer.ValidateOrderByConsumerCommand;

public class ConsumerServiceProxy {
    public final CommandEndpoint<ValidateOrderByConsumerCommand> validateOrder= CommandEndpointBuilder
            .forCommand(ValidateOrderByConsumerCommand.class)
            .withChannel(ConsumerServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
