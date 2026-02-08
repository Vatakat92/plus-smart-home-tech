package ru.yandex.practicum.commerce.payment.service;

import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    BigDecimal productCost(OrderDto order);
    BigDecimal getTotalCost(OrderDto order);
    PaymentDto payment(OrderDto order);
    void paymentFailed(UUID paymentId);
    void paymentSuccess(UUID paymentId);
}
