package ru.yandex.practicum.commerce.payment.validation;

import ru.yandex.practicum.commerce.dto.OrderDto;

import java.util.UUID;

public interface PaymentValidationService {
    void validateOrderId(UUID orderId);
    void validatePaymentId(UUID paymentId);
    void validateOrderDto(OrderDto order);
}
