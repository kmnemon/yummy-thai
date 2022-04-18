package org.keliu.orderservice.controller;

import org.keliu.orderservice.domain.DeliveryInformation;
import org.keliu.orderservice.domain.MenuItemIdAndQuantity;
import org.keliu.orderservice.domain.Order;
import org.keliu.orderservice.dto.CreateOrderRequest;
import org.keliu.orderservice.dto.CreateOrderResponse;
import org.keliu.orderservice.dto.GetOrderResponse;
import org.keliu.orderservice.dto.Mapper;
import org.keliu.orderservice.exception.OrderNotFoundException;
import org.keliu.orderservice.repository.OrderRepository;
import org.keliu.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(method = RequestMethod.POST)
    public CreateOrderResponse create(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getConsumerId(),
                request.getRestaurantId(),
                new DeliveryInformation(request.getDeliveryTime(), request.getDeliveryAddress()),
                request.getLineItems().stream().map(x -> new MenuItemIdAndQuantity(x.getMenuItemId(), x.getQuantity())).collect(toList())
        );
        return new CreateOrderResponse(order.getId());
    }

    @RequestMapping(path = "/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<GetOrderResponse> getOrder(@PathVariable long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.map(o -> new ResponseEntity<>(Mapper.toGetOrderResponse(o), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(path = "/{orderId}/cancel", method = RequestMethod.POST)
    public ResponseEntity<GetOrderResponse> cancel(@PathVariable long orderId) {
        try {
            Order order = orderService.cancel(orderId);
            return new ResponseEntity<>(makeGetOrderResponse(order), HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
