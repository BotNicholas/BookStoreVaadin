package org.nicholas.bookstorevaadin.service.impl;

import org.springframework.stereotype.Service;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapper;
import org.nicholas.bookstorevaadin.model.BookCategory;
import org.nicholas.bookstorevaadin.model.dto.BookCategoryDTO;
import org.nicholas.bookstorevaadin.repository.BookCategoryRepository;
import org.nicholas.bookstorevaadin.service.DefaultService;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class BookCategoryServiceImpl implements DefaultService<BookCategoryDTO, BookCategory, Integer> {
    private final BookCategoryRepository bookCategoryRepository;
    private final AbstractMapper mapper;

    public BookCategoryServiceImpl(BookCategoryRepository bookCategoryRepository, AbstractMapper mapper) {
        this.bookCategoryRepository = bookCategoryRepository;
        this.mapper = mapper;
    }

    @Override
    public List<BookCategoryDTO> findAll() {
        List<BookCategory> bookCategories = bookCategoryRepository.findAll();
        return bookCategories.stream().map(bookCategory -> mapper.toDTO(bookCategory, BookCategoryDTO.class)).collect(Collectors.toList());
    }

    @Override
    public BookCategoryDTO findByKey(Integer key) {
        return mapper.toDTO(bookCategoryRepository.findById(key), BookCategoryDTO.class);
    }

    @Override
    public BookCategoryDTO save(BookCategoryDTO obj) {
        BookCategory bookCategory = bookCategoryRepository.save(mapper.toEntity(obj, BookCategory.class));
        return mapper.toDTO(bookCategory, BookCategoryDTO.class);
    }

    @Override
    public BookCategoryDTO update(Integer key, BookCategoryDTO obj) {
        BookCategory bookCategory = mapper.toEntity(obj, BookCategory.class);
        bookCategory.setCode(key);
        bookCategoryRepository.save(bookCategory);
        return mapper.toDTO(bookCategory, BookCategoryDTO.class);
    }

    @Override
    public void delete(Integer key) {
        bookCategoryRepository.deleteById(key);
    }
}
