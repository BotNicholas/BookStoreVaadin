package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.Contact;
import org.nicholas.bookstorevaadin.model.dto.ContactDTO;

@Component
public class ContactMapper implements DefaultMapper<ContactDTO, Contact> {
    @Autowired
    private ModelMapper mapper;

    @Override
    public Contact toEntity(ContactDTO dto) {
        return (dto == null) ? null : mapper.map(dto, Contact.class);
    }

    @Override
    public ContactDTO toDTO(Contact entity) {
        return (entity == null) ? null : mapper.map(entity, ContactDTO.class);
    }
}
