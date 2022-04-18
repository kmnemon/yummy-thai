package org.keliu.orderservice.dto;

import org.keliu.orderservice.domain.Order;
import org.springframework.stereotype.Component;

public class Mapper {
    public static GetOrderResponse toGetOrderResponse(Order order){
        return new GetOrderResponse(order.getId(), order.getState(), order.getOrderTotal());
    }

}
