package org.keliu.orderservice.dto;

import org.keliu.orderservice.domain.Order;

public class Mapper {
    public static GetOrderResponse toGetOrderResponse(Order order){
        return new GetOrderResponse(order.getId(), order.getState(), order.getOrderTotal());
    }


}
