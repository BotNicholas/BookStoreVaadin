package org.nicholas.bookstorevaadin.service.impl;

import org.springframework.stereotype.Service;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapper;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.OrderItem;
import org.nicholas.bookstorevaadin.model.dto.OrderItemDTO;
import org.nicholas.bookstorevaadin.repository.OrderItemRepository;
import org.nicholas.bookstorevaadin.service.DefaultService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements DefaultService<OrderItemDTO, OrderItem, Integer> {
    private final OrderItemRepository orderItemRepository;
    private final AbstractMapper mapper;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, AbstractMapper mapper) {
        this.orderItemRepository = orderItemRepository;
        this.mapper = mapper;
    }

    @Override
    public List<OrderItemDTO> findAll() {
        List<OrderItem> orderItems = orderItemRepository.findAll();
        return orderItems.stream().map(orderItem -> mapper.toDTO(orderItem, OrderItemDTO.class)).collect(Collectors.toList());
    }

    @Override
    public OrderItemDTO findByKey(Integer key) {
        return mapper.toDTO(orderItemRepository.findById(key), OrderItemDTO.class);
    }

    public List<OrderItemDTO> findAllByCostumer(Costumer costumer) {
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder_Costumer(costumer);
        return orderItems.stream().map(orderItem -> mapper.toDTO(orderItem, OrderItemDTO.class)).collect(Collectors.toList());
    }

    @Override
    public OrderItemDTO save(OrderItemDTO obj) {
        OrderItem orderItem = orderItemRepository.save(mapper.toEntity(obj, OrderItem.class));
        return mapper.toDTO(orderItem, OrderItemDTO.class);
    }

    @Override
    public OrderItemDTO update(Integer key, OrderItemDTO obj) {
        OrderItem orderItem = mapper.toEntity(obj, OrderItem.class);
        orderItem.setId(key);
        orderItemRepository.save(orderItem);
        return mapper.toDTO(orderItem, OrderItemDTO.class);
    }

    @Override
    public void delete(Integer key) {
        orderItemRepository.deleteById(key);
    }
}
