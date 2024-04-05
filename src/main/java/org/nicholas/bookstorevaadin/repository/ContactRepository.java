package org.nicholas.bookstorevaadin.repository;

import org.nicholas.bookstorevaadin.model.RefContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.nicholas.bookstorevaadin.model.Contact;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    public List<Contact> findAllByContactType_Code(Integer type_code);
}
