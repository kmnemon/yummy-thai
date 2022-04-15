package org.keliu.orderservice.repository;

import org.keliu.orderservice.domain.Restaurant;
import org.springframework.data.repository.CrudRepository;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
}
