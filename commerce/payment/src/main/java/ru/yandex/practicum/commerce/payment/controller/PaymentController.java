package ru.yandex.practicum.commerce.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperations;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    @Override
    public BigDecimal productCost(OrderDto order) {
        return paymentService.productCost(order);
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        return paymentService.getTotalCost(order);
    }

    @Override
    public PaymentDto payment(OrderDto order) {
        return paymentService.payment(order);
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        paymentService.paymentFailed(paymentId);
    }

    @Override
    public void paymentSuccess(UUID paymentId) {
        paymentService.paymentSuccess(paymentId);
    }
}
