package org.keliu.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.keliu.orderservice.sagaparticipants.proxy.AccountingServiceProxy;
import org.keliu.orderservice.sagaparticipants.proxy.ConsumerServiceProxy;
import org.keliu.orderservice.sagaparticipants.proxy.KitchenServiceProxy;
import org.keliu.orderservice.sagaparticipants.proxy.OrderServiceProxy;
import org.keliu.common.reply.CreateTicketReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderSaga.class);

    private SagaDefinition<CreateOrderSagaData> sagaDefinition;

    public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService,
                           KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService) {
        this.sagaDefinition =
                step()
                        .withCompensation(orderService.reject, CreateOrderSagaData::makeRejectOrderCommand)
                        .step().invokeParticipant(consumerService.validateOrder, CreateOrderSagaData::makeValidateOrderByConsumerCommand)
                        .step().invokeParticipant(kitchenService.create, CreateOrderSagaData::makeCreateTicketCommand)
                        .onReply(CreateTicketReply.class, CreateOrderSagaData::handleCreateTicketReply)
                        .withCompensation(kitchenService.cancel, CreateOrderSagaData::makeCancelCreateTicketCommand)
                        .step().invokeParticipant(accountingService.authorize, CreateOrderSagaData::makeAuthorizeCommand)
                        .step().invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaData::makeConfirmCreateTicketCommand)
                        .step().invokeParticipant(orderService.approve, CreateOrderSagaData::makeApproveOrderCommand)
                        .build();
    }

    @Override
    public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
        return sagaDefinition;
    }

}
