package ru.yandex.practicum.commerce.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.OrderFeignClient;
import ru.yandex.practicum.commerce.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.commerce.payment.entity.Payment;
import ru.yandex.practicum.commerce.payment.mapper.PaymentMapper;
import ru.yandex.practicum.commerce.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreFeignClient shoppingStoreFeignClient;
    private final OrderFeignClient orderFeignClient;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal productCost(OrderDto order) {
        log.info("Calculating product cost for order: {}", order.getOrderId());

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
    @Transactional(readOnly = true)
    public BigDecimal getTotalCost(OrderDto order) {
        log.info("Calculating total cost for order: {}", order.getOrderId());

        // Calculate products cost
        BigDecimal productCost = productCost(order);

        // Calculate VAT (10%)
        BigDecimal vat = productCost.multiply(BigDecimal.valueOf(0.10));

        // Total with VAT
        BigDecimal totalWithVat = productCost.add(vat);

        // Add delivery cost
        BigDecimal deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal totalCost = totalWithVat.add(deliveryCost);

        return totalCost;
    }

    @Override
    @Transactional
    public PaymentDto payment(OrderDto order) {
        log.info("Creating payment for order: {}", order.getOrderId());

        BigDecimal productCost = productCost(order);
        BigDecimal deliveryCost = order.getDeliveryPrice() != null ? order.getDeliveryPrice() : BigDecimal.ZERO;
        BigDecimal totalCost = getTotalCost(order);

        // Calculate VAT (10%)
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
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setPaymentState(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Notify order service about failed payment
        // This would typically be done via event or direct call
    }

    @Override
    @Transactional
    public void paymentSuccess(UUID paymentId) {
        log.info("Marking payment as success: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        payment.setPaymentState(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        // Notify order service about successful payment
        // This would typically be done via event or direct call
    }
}
