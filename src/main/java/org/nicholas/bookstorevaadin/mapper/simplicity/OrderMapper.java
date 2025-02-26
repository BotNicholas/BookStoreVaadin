package org.nicholas.bookstorevaadin.mapper.simplicity;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.nicholas.bookstorevaadin.model.Order;
import org.nicholas.bookstorevaadin.model.dto.OrderDTO;

@Component
public class OrderMapper implements DefaultMapper<OrderDTO, Order> {
    @Autowired
    private ModelMapper mapper;

    @Override
    public Order toEntity(OrderDTO dto) {
        return (dto == null) ? null : mapper.map(dto, Order.class);
    }

    @Override
    public OrderDTO toDTO(Order entity) {
        return (entity == null) ? null : mapper.map(entity, OrderDTO.class);
    }
}
