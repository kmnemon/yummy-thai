package org.keliu.orderhistory.messaging;

import org.keliu.orderhistory.dao.OrderHistoryDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({CommonConfiguration.class, TramNoopDuplicateMessageDetectorConfiguration.class, TramEventSubscriberConfiguration.class})
public class OrderHistoryServiceMessagingConfiguration {

    @Bean
    public OrderHistoryEventHandlers orderHistoryEventHandlers(OrderHistoryDao orderHistoryDao) {
        return new OrderHistoryEventHandlers(orderHistoryDao);
    }

    @Bean
    public DomainEventDispatcher orderHistoryDomainEventDispatcher(OrderHistoryEventHandlers orderHistoryEventHandlers, DomainEventDispatcherFactory domainEventDispatcherFactory) {
        return domainEventDispatcherFactory.make("orderHistoryDomainEventDispatcher", orderHistoryEventHandlers.domainEventHandlers());
    }

}
