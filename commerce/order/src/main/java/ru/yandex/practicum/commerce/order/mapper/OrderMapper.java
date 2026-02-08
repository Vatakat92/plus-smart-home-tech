package ru.yandex.practicum.commerce.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.order.entity.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "shoppingCartId", source = "shoppingCartId")
    @Mapping(target = "products", source = "products")
    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "deliveryId", source = "deliveryId")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "deliveryWeight", source = "deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "deliveryVolume")
    @Mapping(target = "fragile", source = "fragile")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "deliveryPrice", source = "deliveryPrice")
    @Mapping(target = "productPrice", source = "productPrice")
    OrderDto toDto(Order order);
}
