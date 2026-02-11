package ru.yandex.practicum.commerce.delivery.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.commerce.delivery.entity.Address;
import ru.yandex.practicum.commerce.delivery.entity.Delivery;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.DeliveryDto;

@Mapper(componentModel = "spring")
public interface DeliveryMapper {

    DeliveryDto toDto(Delivery delivery);

    default Address toEntity(AddressDto dto) {
        if (dto == null) return null;
        return Address.builder()
                .country(dto.getCountry())
                .city(dto.getCity())
                .street(dto.getStreet())
                .house(dto.getHouse())
                .flat(dto.getFlat())
                .build();
    }
}
