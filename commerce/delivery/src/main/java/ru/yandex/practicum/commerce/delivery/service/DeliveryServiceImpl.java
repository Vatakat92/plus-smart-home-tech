package ru.yandex.practicum.commerce.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.delivery.entity.Delivery;
import ru.yandex.practicum.commerce.delivery.mapper.DeliveryMapper;
import ru.yandex.practicum.commerce.delivery.repository.DeliveryRepository;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.OrderFeignClient;
import ru.yandex.practicum.commerce.feign.WarehouseFeignClient;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final WarehouseFeignClient warehouseFeignClient;
    private final OrderFeignClient orderFeignClient;

    private static final String ADDRESS_1 = "ADDRESS_1";
    private static final String ADDRESS_2 = "ADDRESS_2";
    private static final BigDecimal BASE_RATE = BigDecimal.valueOf(5.0);

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        log.info("Planning delivery for order: {}", deliveryDto.getOrderId());

        Delivery delivery = Delivery.builder()
                .fromAddress(deliveryMapper.toEntity(deliveryDto.getFromAddress()))
                .toAddress(deliveryMapper.toEntity(deliveryDto.getToAddress()))
                .orderId(deliveryDto.getOrderId())
                .deliveryState(deliveryDto.getDeliveryState() != null ? deliveryDto.getDeliveryState() : DeliveryState.CREATED)
                .build();

        delivery = deliveryRepository.save(delivery);
        log.info("Delivery created: {}", delivery.getDeliveryId());

        return deliveryMapper.toDto(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal deliveryCost(OrderDto order) {
        log.info("Calculating delivery cost for order: {}", order.getOrderId());

        // Get delivery info
        Delivery delivery = deliveryRepository.findByOrderId(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + order.getOrderId()));

        AddressDto warehouseAddress = warehouseFeignClient.getAddress();

        // Base calculation
        BigDecimal cost = BASE_RATE;

        // Step 1: Apply warehouse address multiplier
        String warehouseStreet = warehouseAddress.getStreet();
        if (warehouseStreet != null) {
            if (warehouseStreet.contains(ADDRESS_1)) {
                cost = cost.multiply(BigDecimal.ONE);
            } else if (warehouseStreet.contains(ADDRESS_2)) {
                cost = cost.multiply(BigDecimal.valueOf(2));
            }
        }
        cost = cost.add(BASE_RATE);

        // Step 2: Fragile products
        if (Boolean.TRUE.equals(order.getFragile())) {
            BigDecimal fragileCost = cost.multiply(BigDecimal.valueOf(0.2));
            cost = cost.add(fragileCost);
        }

        // Step 3: Weight
        Double weight = order.getDeliveryWeight() != null ? order.getDeliveryWeight() : 0.0;
        cost = cost.add(BigDecimal.valueOf(weight).multiply(BigDecimal.valueOf(0.3)));

        // Step 4: Volume
        Double volume = order.getDeliveryVolume() != null ? order.getDeliveryVolume() : 0.0;
        cost = cost.add(BigDecimal.valueOf(volume).multiply(BigDecimal.valueOf(0.2)));

        // Step 5: Address check
        String toStreet = delivery.getToAddress() != null ? delivery.getToAddress().getStreet() : null;
        if (warehouseStreet != null && toStreet != null && !warehouseStreet.equals(toStreet)) {
            BigDecimal addressCost = cost.multiply(BigDecimal.valueOf(0.2));
            cost = cost.add(addressCost);
        }

        return cost;
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        log.info("Delivery picked for order: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        // Notify warehouse that products are shipped
        ShippedToDeliveryRequest shippedRequest = ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(delivery.getDeliveryId())
                .build();
        warehouseFeignClient.shippedToDelivery(shippedRequest);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        log.info("Delivery successful for order: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);

        // Update order status
        orderFeignClient.delivery(orderId);
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        log.info("Delivery failed for order: {}", orderId);

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));

        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);

        // Update order status
        orderFeignClient.deliveryFailed(orderId);
    }
}
