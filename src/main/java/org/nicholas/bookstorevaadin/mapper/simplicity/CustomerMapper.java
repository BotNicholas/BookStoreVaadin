package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.dto.CostumerDTO;

@Component
public class CustomerMapper implements DefaultMapper<CostumerDTO, Costumer> {
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public Costumer toEntity(CostumerDTO dto) {
        return (dto == null) ? null : modelMapper.map(dto, Costumer.class);
    }

    @Override
    public CostumerDTO toDTO(Costumer entity) {
        return (entity == null) ? null : modelMapper.map(entity, CostumerDTO.class);
    }
}
