package org.keliu.orderservice.service;

import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keliu.common.domain.delivery.DeliveryInformation;
import org.keliu.orderservice.domain.MenuItemIdAndQuantity;
import org.keliu.orderservice.domain.Order;
import org.keliu.orderservice.domain.OrderDetails;
import org.keliu.orderservice.domain.Restaurant;
import org.keliu.orderservice.events.OrderCreatedEvent;
import org.keliu.orderservice.events.OrderDomainEvent;
import org.keliu.orderservice.events.OrderDomainEventPublisher;
import org.keliu.orderservice.helper.YummyFactory;
import org.keliu.orderservice.repository.OrderRepository;
import org.keliu.orderservice.repository.RestaurantRepository;
import org.keliu.orderservice.sagas.cancelorder.CancelOrderSaga;
import org.keliu.orderservice.sagas.createorder.CreateOrderSaga;
import org.keliu.orderservice.sagas.reviseorder.ReviseOrderSaga;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Test
    public void createOrderTest(){
        //mock shared out-processing dependency
        OrderDomainEventPublisher orderDomainEventPublisher = mock(OrderDomainEventPublisher.class);

        long consumerId = 1;
        Restaurant restaurant = YummyFactory.createRestaurant();
        DeliveryInformation deliveryInformation = YummyFactory.createDeliveryInformation();
        List<MenuItemIdAndQuantity> menuItemIdAndQuantities = YummyFactory.createMenuItemIdAndQuantities();


        Order order = orderService.createOrder(consumerId, restaurant.getId(), deliveryInformation, menuItemIdAndQuantities);
        List<OrderDomainEvent> events = Collections.singletonList(new OrderCreatedEvent(new OrderDetails(order.getConsumerId(), order.getRestaurantId(), order.getLineItems(), order.getOrderTotal()),
                order.getDeliveryInformation().getDeliveryAddress(),
                restaurant.getName()
        )) ;

        verify(orderDomainEventPublisher).publish(order, events);
    }




}
