package ru.yandex.practicum.commerce.delivery.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.AddressDto;
import ru.yandex.practicum.commerce.dto.DeliveryDto;
import ru.yandex.practicum.commerce.delivery.exception.InvalidDeliveryException;

import java.util.UUID;

@Slf4j
@Service
public class DeliveryValidationServiceImpl implements DeliveryValidationService {

    @Override
    public void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new InvalidDeliveryException("Order ID cannot be null");
        }
    }

    @Override
    public void validateDeliveryDto(DeliveryDto deliveryDto) {
        if (deliveryDto == null) {
            throw new InvalidDeliveryException("Delivery DTO cannot be null");
        }
        validateOrderId(deliveryDto.getOrderId());
        validateAddresses(deliveryDto.getFromAddress(), deliveryDto.getToAddress());
    }

    @Override
    public void validateAddresses(AddressDto fromAddress, AddressDto toAddress) {
        if (fromAddress == null) {
            throw new InvalidDeliveryException("From address cannot be null");
        }
        if (toAddress == null) {
            throw new InvalidDeliveryException("To address cannot be null");
        }
        validateAddress(fromAddress, "From");
        validateAddress(toAddress, "To");
    }

    private void validateAddress(AddressDto address, String prefix) {
        if (address.getCountry() == null || address.getCountry().trim().isEmpty()) {
            throw new InvalidDeliveryException(prefix + " address country cannot be null or empty");
        }
        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new InvalidDeliveryException(prefix + " address city cannot be null or empty");
        }
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new InvalidDeliveryException(prefix + " address street cannot be null or empty");
        }
    }
}
