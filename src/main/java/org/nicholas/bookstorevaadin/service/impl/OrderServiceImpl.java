package org.nicholas.bookstorevaadin.service.impl;

import org.springframework.stereotype.Service;
import org.nicholas.bookstorevaadin.mapper.abstraction.AbstractMapper;
import org.nicholas.bookstorevaadin.model.Costumer;
import org.nicholas.bookstorevaadin.model.Order;
import org.nicholas.bookstorevaadin.model.OrderItem;
import org.nicholas.bookstorevaadin.model.dto.CostumerDTO;
import org.nicholas.bookstorevaadin.model.dto.FullOrderDTO;
import org.nicholas.bookstorevaadin.model.dto.MyFullOrderDTO;
import org.nicholas.bookstorevaadin.repository.OrderRepository;
import org.nicholas.bookstorevaadin.service.DefaultService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements DefaultService<FullOrderDTO, Order, Integer> {
    private final OrderRepository orderRepository;
    private final AbstractMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository, AbstractMapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Override
    public List<FullOrderDTO> findAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> mapper.toDTO(order, FullOrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public FullOrderDTO findByKey(Integer key) {
        return mapper.toDTO(orderRepository.findById(key).orElse(null), FullOrderDTO.class);
    }

    public List<FullOrderDTO> findAllByCostumer(Costumer costumer) {
        return orderRepository.findAllByCostumer(costumer).stream().map(order -> mapper.toDTO(order, FullOrderDTO.class)).collect(Collectors.toList());
    }

    @Override
    public FullOrderDTO save(FullOrderDTO obj) {
        Order order = mapper.toEntity(obj, Order.class);
        for (OrderItem orderItem : order.getItemList()) {
            orderItem.setOrder(order);
        }
        order = orderRepository.save(order);
        return mapper.toDTO(order, FullOrderDTO.class);
    }

    public FullOrderDTO saveForCustomer(FullOrderDTO orderDTO, Costumer costumer) {
        costumer.setOrders(Collections.EMPTY_LIST);
        CostumerDTO costumerDTO = mapper.toDTO(costumer, CostumerDTO.class);
        orderDTO.setCostumer(costumerDTO);
        return save(orderDTO);
    }

    public FullOrderDTO saveForCustomer(MyFullOrderDTO myOrderDTO, Costumer costumer) {
        return saveForCustomer(mapper.toDTO(myOrderDTO, FullOrderDTO.class), costumer);
    }

    @Override
    public FullOrderDTO update(Integer key, FullOrderDTO obj) {
        Order order = mapper.toEntity(obj, Order.class);
        for (OrderItem orderItem : order.getItemList()) {
            orderItem.setOrder(order);
        }
        order.setId(key);
        orderRepository.save(order);
        return mapper.toDTO(order, FullOrderDTO.class);
    }

    @Override
    public void delete(Integer key) {
        orderRepository.deleteById(key);
    }
}
