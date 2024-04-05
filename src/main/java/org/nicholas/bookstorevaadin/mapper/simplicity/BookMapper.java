package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.Book;
import org.nicholas.bookstorevaadin.model.dto.BookDTO;

@Component
public class BookMapper implements DefaultMapper<BookDTO, Book> {
    @Autowired
    private ModelMapper mapper;

    @Override
    public Book toEntity(BookDTO dto) {
        return (dto == null) ? null : mapper.map(dto, Book.class);
    }

    @Override
    public BookDTO toDTO(Book entity) {
        return (entity == null) ? null : mapper.map(entity, BookDTO.class);
    }
}
