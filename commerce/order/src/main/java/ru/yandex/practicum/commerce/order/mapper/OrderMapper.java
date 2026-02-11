package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.order.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDto toDto(Order order);
}
