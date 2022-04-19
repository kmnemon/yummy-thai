package org.keliu.orderservice.sagaparticipants.proxy;

import io.eventuate.tram.commands.common.Success;
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint;
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder;
import org.keliu.common.command.accounting.AuthorizeCommand;
import org.keliu.common.channel.AccountingServiceChannels;

public class AccountingServiceProxy {
    public final CommandEndpoint<AuthorizeCommand> authorize= CommandEndpointBuilder
            .forCommand(AuthorizeCommand.class)
            .withChannel(AccountingServiceChannels.COMMAND_CHANNEL)
            .withReply(Success.class)
            .build();
}
