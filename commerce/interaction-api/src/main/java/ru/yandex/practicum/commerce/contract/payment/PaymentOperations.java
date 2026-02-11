package ru.yandex.practicum.commerce.contract.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {

    @PostMapping("/api/v1/payment/productCost")
    BigDecimal productCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/totalCost")
    BigDecimal getTotalCost(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment")
    PaymentDto payment(@Valid @RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@Valid @RequestBody @NotNull UUID paymentId);

    @PostMapping("/api/v1/payment/refund")
    void paymentSuccess(@Valid @RequestBody @NotNull UUID paymentId);
}
