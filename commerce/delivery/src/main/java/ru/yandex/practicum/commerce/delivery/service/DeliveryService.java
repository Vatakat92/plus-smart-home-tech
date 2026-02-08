package ru.yandex.practicum.commerce.delivery.service;

import ru.yandex.practicum.commerce.dto.DeliveryDto;
import ru.yandex.practicum.commerce.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {
    DeliveryDto planDelivery(DeliveryDto deliveryDto);
    BigDecimal deliveryCost(OrderDto order);
    void deliveryPicked(UUID orderId);
    void deliverySuccessful(UUID orderId);
    void deliveryFailed(UUID orderId);
}
