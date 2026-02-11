package ru.yandex.practicum.commerce.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperations;
import ru.yandex.practicum.commerce.dto.DeliveryDto;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.delivery.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    public BigDecimal deliveryCost(OrderDto order) {
        return deliveryService.deliveryCost(order);
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }
}
