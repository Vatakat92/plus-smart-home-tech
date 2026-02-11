package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.commerce.payment.entity.Payment;
import ru.yandex.practicum.commerce.payment.exception.PaymentNotFoundException;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;
import ru.yandex.practicum.commerce.payment.validation.PaymentValidationService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private final PaymentValidationService paymentValidationService;

    @Override
    public BigDecimal productCost(OrderDto order) {
        log.info("Calculating product cost for order: {}", order.getOrderId());
        paymentValidationService.validateOrderDto(order);

        BigDecimal totalCost = BigDecimal.ZERO;

        for (UUID productId : order.getProducts().keySet()) {
            ProductDto product = shoppingStoreFeignClient.getProductById(productId);
            Long quantity = order.getProducts().get(productId);
            BigDecimal productTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalCost = totalCost.add(productTotal);
        }

        return totalCost;
    }

    @Override
    public BigDecimal getTotalCost(OrderDto order) {
        log.info("Calculating total cost for order: {}", order.getOrderId());
        paymentValidationService.validateOrderDto(order);

        BigDecimal productCost = productCost(order);
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));
        BigDecimal totalWithVat = productCost.add(vat);
        BigDecimal deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;

        return totalWithVat.add(deliveryCost);
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info("Creating payment for order: {}", order.getOrderId());
        paymentValidationService.validateOrderDto(order);

        BigDecimal productCost = productCost(order);
        BigDecimal deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal totalCost = getTotalCost(order);
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));

        Payment payment = Payment.builder()
                .totalPayment(totalCost)
                .deliveryTotal(deliveryCost)
                .feeTotal(vat)
                .paymentState(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        log.info("Payment created: {}", payment.getPaymentId());

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public void paymentFailed(UUID paymentId) {
        log.info("Marking payment as failed: {}", paymentId);
        paymentValidationService.validatePaymentId(paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        payment.setPaymentState(PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("Marking payment as success: {}", paymentId);
        paymentValidationService.validatePaymentId(paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));

        payment.setPaymentState(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }
}
