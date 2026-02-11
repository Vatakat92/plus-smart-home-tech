package ru.yandex.practicum.commerce.cart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {

    @Mapping(target = "shoppingCartId", source = "cartId")
    @Mapping(target = "products", source = "products")
    ShoppingCartDto toDto(ShoppingCartEntity entity);

}