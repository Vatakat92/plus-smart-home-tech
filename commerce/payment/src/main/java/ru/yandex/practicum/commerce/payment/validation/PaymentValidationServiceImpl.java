package ru.yandex.practicum.commerce.payment.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.payment.exception.InvalidPaymentException;

import java.util.UUID;

@Slf4j
@Service
public class PaymentValidationServiceImpl implements PaymentValidationService {

    @Override
    public void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new InvalidPaymentException("Order ID cannot be null");
        }
    }

    @Override
    public void validatePaymentId(UUID paymentId) {
        if (paymentId == null) {
            throw new InvalidPaymentException("Payment ID cannot be null");
        }
    }

    @Override
    public void validateOrderDto(OrderDto order) {
        if (order == null) {
            throw new InvalidPaymentException("Order DTO cannot be null");
        }
        validateOrderId(order.getOrderId());
        if (order.getProducts() == null || order.getProducts().isEmpty()) {
            throw new InvalidPaymentException("Order products cannot be null or empty");
        }
    }
}
