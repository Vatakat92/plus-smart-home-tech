package ru.yandex.practicum.commerce.delivery.validation;

import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.DeliveryDto;

import java.util.UUID;

public interface DeliveryValidationService {
    void validateOrderId(UUID orderId);
    void validateDeliveryDto(DeliveryDto deliveryDto);
    void validateAddresses(AddressDto fromAddress, AddressDto toAddress);
}
