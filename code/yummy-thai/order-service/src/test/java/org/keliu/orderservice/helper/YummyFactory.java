package org.keliu.orderservice.helper;

import org.keliu.common.domain.Address;
import org.keliu.common.domain.Money;
import org.keliu.common.domain.delivery.DeliveryInformation;
import org.keliu.orderservice.domain.MenuItem;
import org.keliu.orderservice.domain.MenuItemIdAndQuantity;
import org.keliu.orderservice.domain.OrderLineItem;
import org.keliu.orderservice.domain.Restaurant;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YummyFactory {
    public static Restaurant createRestaurant(){
        return new Restaurant(1, "kfc", createMenuItems());
    }

    public static List<MenuItem> createMenuItems(){
        MenuItem menuItem1 = new MenuItem("menuitem01", "pine apple", new Money(10));
        MenuItem menuItem2 = new MenuItem("menuitem02", "water melon", new Money(20));
        MenuItem menuItem3 = new MenuItem("menuitem03", "strawberry", new Money(30));

        return Stream.of(menuItem1, menuItem2, menuItem3).collect(Collectors.toList());
    }

    public static DeliveryInformation createDeliveryInformation(){
        LocalDateTime localDateTime = LocalDateTime.of(2022, 4, 19, 20, 39);
        Address address = new Address("street1", "street2", "city", "state","zip001");
        return new DeliveryInformation(localDateTime, address);
    }

    public static List<OrderLineItem> createOrderLineItem(){
        OrderLineItem orderLineItem1 = new OrderLineItem("menuItemId01", "beafcook", new Money(223), 3);
        OrderLineItem orderLineItem2 = new OrderLineItem("menuItemId01", "cornsoup", new Money(123), 1);
        OrderLineItem orderLineItem3 = new OrderLineItem("menuItemId01", "lemoned", new Money(23), 5);

        return Stream.of(orderLineItem1, orderLineItem2, orderLineItem3).collect(Collectors.toList());
    }

    public static List<MenuItemIdAndQuantity> createMenuItemIdAndQuantities(){
        MenuItemIdAndQuantity menuItemIdAndQuantity1 = new MenuItemIdAndQuantity("menuItemId01", 3);
        MenuItemIdAndQuantity menuItemIdAndQuantity2 = new MenuItemIdAndQuantity("menuItemId02", 1);
        MenuItemIdAndQuantity menuItemIdAndQuantity3 = new MenuItemIdAndQuantity("menuItemId03", 5);

        return Stream.of(menuItemIdAndQuantity1, menuItemIdAndQuantity2, menuItemIdAndQuantity3).collect(Collectors.toList());
    }
}
