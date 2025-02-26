package org.nicholas.bookstorevaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.Order;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("select distinct o from Order o left join fetch o.itemList where o.costumer = :costumer") //even if order don't have any order item we whatever must return this order
    List<Order> findAllByCostumer(Costumer costumer);

    @Query("select distinct o from Order o left join fetch o.itemList")
    List<Order> findFullAllOrders();

    @Query("select o from Order o join fetch o.itemList where o.id = :id")
    Order findFullById(Integer id);
}
