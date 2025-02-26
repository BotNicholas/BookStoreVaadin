package org.nicholas.bookstorevaadin.service.impl;

import org.springframework.stereotype.Service;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapper;
import org.nicholas.bookstorevaadin.model.RefContactType;
import org.nicholas.bookstorevaadin.model.dto.RefContactTypeDTO;
import org.nicholas.bookstorevaadin.repository.RefContactTypeRepository;
import org.nicholas.bookstorevaadin.service.DefaultService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RefContactTypeServiceImpl implements DefaultService<RefContactTypeDTO, RefContactType, Integer> {
    private final RefContactTypeRepository refContactTypeRepository;
    private final AbstractMapper mapper;

    public RefContactTypeServiceImpl(RefContactTypeRepository refContactTypeRepository, AbstractMapper mapper) {
        this.refContactTypeRepository = refContactTypeRepository;
        this.mapper = mapper;
    }

    @Override
    public List<RefContactTypeDTO> findAll() {
        List<RefContactType> refContactTypes = refContactTypeRepository.findAll();
        return refContactTypes.stream().map(refContactType -> mapper.toDTO(refContactType, RefContactTypeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public RefContactTypeDTO findByKey(Integer key) {
        return mapper.toDTO(refContactTypeRepository.findById(key), RefContactTypeDTO.class);
    }

    @Override
    public RefContactTypeDTO save(RefContactTypeDTO obj) {
        RefContactType refContactType = refContactTypeRepository.save(mapper.toEntity(obj, RefContactType.class));
        return mapper.toDTO(refContactType, RefContactTypeDTO.class);
    }

    @Override
    public RefContactTypeDTO update(Integer key, RefContactTypeDTO obj) {
        RefContactType refContactType = mapper.toEntity(obj, RefContactType.class);
        refContactType.setCode(key);
        refContactTypeRepository.save(refContactType);
        return mapper.toDTO(refContactType, RefContactTypeDTO.class);
    }

    @Override
    public void delete(Integer key) {
        refContactTypeRepository.deleteById(key);
    }
}
