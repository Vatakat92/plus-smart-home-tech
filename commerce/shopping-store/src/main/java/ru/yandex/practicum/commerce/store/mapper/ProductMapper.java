package ru.yandex.practicum.commerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.store.entity.ProductEntity;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "imageSrc", source = "imageSrc")
    @Mapping(target = "quantityState", source = "quantityState")
    @Mapping(target = "productState", source = "productState")
    @Mapping(target = "productCategory", source = "productCategory")
    @Mapping(target = "price", source = "price")
    ProductDto toDto(ProductEntity entity);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productName", source = "productName")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "imageSrc", source = "imageSrc")
    @Mapping(target = "quantityState", source = "quantityState")
    @Mapping(target = "productState", source = "productState")
    @Mapping(target = "productCategory", source = "productCategory")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductEntity toEntity(ProductDto dto);
}