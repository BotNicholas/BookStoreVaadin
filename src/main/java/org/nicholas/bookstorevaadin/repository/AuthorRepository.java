package org.nicholas.bookstorevaadin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
}
