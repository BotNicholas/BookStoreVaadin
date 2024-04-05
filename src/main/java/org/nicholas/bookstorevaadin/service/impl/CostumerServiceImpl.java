package org.nicholas.bookstorevaadin.service.impl;

import org.hibernate.Hibernate;
import org.nicholas.bookstorevaadin.security.models.StoreUser;
import org.nicholas.bookstorevaadin.security.repositories.StoreUserRepository;
import org.springframework.stereotype.Service;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapper;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.dto.CostumerDTO;
import org.nicholas.bookstorevaadin.repository.CostumerRepository;
import org.nicholas.bookstorevaadin.service.DefaultService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CostumerServiceImpl implements DefaultService<CostumerDTO, Costumer, Integer> {
    private final CostumerRepository costumerRepository;
    private final StoreUserRepository userRepository;
    private final AbstractMapper mapper;

    public CostumerServiceImpl(CostumerRepository costumerRepository, StoreUserRepository userRepository, AbstractMapper mapper) {
        this.costumerRepository = costumerRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CostumerDTO> findAll() {
        List<Costumer> costumers = costumerRepository.findAll();
        return costumers.stream().map(costumer -> {
            Hibernate.initialize(costumer.getOrders());
            return mapper.toDTO(costumer, CostumerDTO.class);
        }).collect(Collectors.toList());
    }

    @Override
    public CostumerDTO findByKey(Integer key) {
        Costumer costumer = costumerRepository.findById(key).orElse(null);
        Hibernate.initialize(costumer.getOrders());
        return mapper.toDTO(costumer, CostumerDTO.class);
    }

    @Override
    public CostumerDTO save(CostumerDTO obj) {
        Costumer costumer = costumerRepository.save(mapper.toEntity(obj, Costumer.class));
        return mapper.toDTO(costumer, CostumerDTO.class);
    }

    @Override
    public CostumerDTO update(Integer key, CostumerDTO obj) {
        Costumer costumer = mapper.toEntity(obj, Costumer.class);
        costumer.setId(key);
        costumerRepository.save(costumer);
        return mapper.toDTO(costumer, CostumerDTO.class);
    }

    @Transactional
    public void deleteUserForCustomer(Costumer costumer) {
        StoreUser user = userRepository.findByCostumer(costumer);
        user.setUsername("AAA");
    }

    @Override
    public void delete(Integer key) {
        costumerRepository.deleteById(key);
    }
}
