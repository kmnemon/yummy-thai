package org.keliu.orderservice.sagas.createorder;

import io.eventuate.tram.sagas.orchestration.SagaDefinition;
import io.eventuate.tram.sagas.simpledsl.SimpleSaga;
import org.keliu.common.sagaparticipants.proxy.AccountingServiceProxy;
import org.keliu.common.sagaparticipants.proxy.ConsumerServiceProxy;
import org.keliu.common.sagaparticipants.proxy.KitchenServiceProxy;
import org.keliu.common.sagaparticipants.proxy.OrderServiceProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaState> {
    private static final Logger logger = LoggerFactory.getLogger(CreateOrderSaga.class);

    private SagaDefinition<CreateOrderSagaState> sagaDefinition;

    public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService,
                           KitchenServiceProxy kitchenService, AccountingServiceProxy accountingService){
        this.sagaDefinition =
                step()
                .withCompensation(orderService.reject, CreateOrderSagaState::)

    }

    @Override
    public SagaDefinition<CreateOrderSagaState> getSagaDefinition() {
        return sagaDefinition;
    }

}
