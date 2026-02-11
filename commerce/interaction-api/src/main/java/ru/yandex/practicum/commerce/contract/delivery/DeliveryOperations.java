package ru.yandex.practicum.commerce.contract.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.DeliveryDto;
import ru.yandex.practicum.commerce.dto.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryOperations {

    @PutMapping("/api/v1/delivery")
    DeliveryDto planDelivery(@Valid @RequestBody DeliveryDto deliveryDto);

    @PostMapping("/api/v1/delivery/cost")
    BigDecimal deliveryCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/delivery/picked")
    void deliveryPicked(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/successful")
    void deliverySuccessful(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/delivery/failed")
    void deliveryFailed(@Valid @RequestBody @NotNull UUID orderId);
}
