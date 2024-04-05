package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.RefContactType;
import org.nicholas.bookstorevaadin.model.dto.RefContactTypeDTO;

@Component
public class RefContactTypeMapper implements DefaultMapper<RefContactTypeDTO, RefContactType> {
    @Autowired
    private ModelMapper mapper;

    @Override
    public RefContactType toEntity(RefContactTypeDTO dto) {
        return (dto == null) ? null : mapper.map(dto, RefContactType.class);
    }

    @Override
    public RefContactTypeDTO toDTO(RefContactType entity) {
        return (entity == null) ? null : mapper.map(entity, RefContactTypeDTO.class);
    }
}
