package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.Author;
import org.nicholas.bookstorevaadin.model.dto.AuthorDTO;

@Component
public class AuthorMapper implements DefaultMapper<AuthorDTO, Author> {
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Author toEntity(AuthorDTO dto) {
        return (dto == null) ? null : modelMapper.map(dto, Author.class);
    }

    @Override
    public AuthorDTO toDTO(Author entity) {
        return (entity == null) ? null : modelMapper.map(entity, AuthorDTO.class);
    }
}
