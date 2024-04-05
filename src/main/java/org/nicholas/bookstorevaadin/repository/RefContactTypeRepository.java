package org.nicholas.bookstorevaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.RefContactType;

@Repository
public interface RefContactTypeRepository extends JpaRepository<RefContactType, Integer> {
}
