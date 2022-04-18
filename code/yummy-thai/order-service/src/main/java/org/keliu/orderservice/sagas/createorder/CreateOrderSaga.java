package org.keliu.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.keliu.reply.CreateTicketReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.keliu.orderservice.sagaparticipants.proxy.*;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderSaga.class);

    private SagaDefinition<CreateOrderSagaState> sagaDefinition;

    public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService,
                           KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
        this.sagaDefinition =
                step()
                        .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
                        .step().invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
                        .step().invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
                        .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
                        .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
                        .step().invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
                        .step().invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand)
                        .step().invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand)
                        .build();
    }

    @Override
    public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
        return sagaDefinition;
    }

}
