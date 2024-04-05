package org.nicholas.bookstorevaadin.repository;

import org.nicholas.bookstorevaadin.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    public List<Book> findAllByCategory_Code(Integer code);
}
