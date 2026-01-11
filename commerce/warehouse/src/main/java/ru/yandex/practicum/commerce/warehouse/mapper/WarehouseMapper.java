package ru.yandex.practicum.commerce.warehouse.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.NewProductInWarehouseRequest;
import ru.yandex.practicum.commerce.warehouse.entity.ProductOnWarehouseEntity;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(source = "productId", target = "productId")
    @Mapping(source = "fragile", target = "fragile")
    @Mapping(source = "dimension.width", target = "width")
    @Mapping(source = "dimension.height", target = "height")
    @Mapping(source = "dimension.depth", target = "depth")
    @Mapping(source = "weight", target = "weight")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantity", constant = "0")
    @Mapping(target = "address", expression = "java(ru.yandex.practicum.commerce.warehouse.util.RandomAddressGenerator.getCurrentAddress())")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductOnWarehouseEntity toEntity(NewProductInWarehouseRequest request);
}