package org.keliu.orderservice.domain;

import org.keliu.orderservice.events.OrderDomainEvent;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "order_service_restaurants")
@Access(AccessType.FIELD)
public class Restaurant {
    @Id
    private long id;
    
    @ElementCollection
    @CollectionTable(name = "order_service_restaurant_menu_items")
    private List<MenuItem> menuItems;

    private String name;

    public Restaurant(){
    }

    public Restaurant(long id, String name, List<MenuItem> menuItems){
        this.id = id;
        this.name = name;
        this.menuItems = menuItems;
    }

    public List<OrderDomainEvent> reviseMenu(List<MenuItem> revisedMenu) {
        throw new UnsupportedOperationException();
    }

    public long getId() {
        return id;
    }

    public Optional<MenuItem> findMenuItem(String menuItemId) {
        return menuItems.stream().filter(mi -> mi.getId().equals(menuItemId)).findFirst();
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public String getName() {
        return name;
    }

}
