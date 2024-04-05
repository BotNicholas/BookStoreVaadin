package org.nicholas.bookstorevaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.Costumer;

@Repository
public interface CostumerRepository extends JpaRepository<Costumer, Integer> {
}
