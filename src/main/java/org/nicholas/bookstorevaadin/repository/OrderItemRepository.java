package org.nicholas.bookstorevaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    List<OrderItem> findAllByOrder_Costumer(Costumer costumer);
}
