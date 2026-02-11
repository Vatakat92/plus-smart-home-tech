package ru.yandex.practicum.commerce.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.*;
import ru.yandex.practicum.commerce.order.entity.Order;
import ru.yandex.practicum.commerce.order.exception.OrderNotFoundException;
import ru.yandex.practicum.commerce.order.mapper.OrderMapper;
import ru.yandex.practicum.commerce.order.repository.OrderRepository;
import ru.yandex.practicum.commerce.order.validation.OrderValidationService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final WarehouseFeignClient warehouseFeignClient;
    private final PaymentFeignClient paymentFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;
    private final ShoppingCartFeignClient shoppingCartFeignClient;
    private final OrderValidationService orderValidationService;

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        log.info("Creating new order for shopping cart: {}", request.getShoppingCartId());
        orderValidationService.validateCreateOrderRequest(request);

        ShoppingCartDto cart = shoppingCartFeignClient.getCart(request.getShoppingCartId().toString());
        BookedProductsDto bookedProducts = warehouseFeignClient.checkAvailability(cart);

        Order order = Order.builder()
                .shoppingCartId(request.getShoppingCartId())
                .products(cart.getProducts())
                .state(OrderState.NEW)
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .fragile(bookedProducts.getFragile())
                .build();

        order = orderRepository.save(order);
        log.info("Order created: {}", order.getOrderId());
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getClientOrders(String username) {
        log.info("Getting orders for user: {}", username);
        orderValidationService.validateUsername(username);
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto payment(UUID orderId) {
        log.info("Processing payment for order: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.PAID);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto paymentFailed(UUID orderId) {
        log.info("Payment failed for order: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto delivery(UUID orderId) {
        log.info("Order delivered: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto deliveryFailed(UUID orderId) {
        log.info("Delivery failed for order: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto completed(UUID orderId) {
        log.info("Order completed: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.COMPLETED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateTotalCost(UUID orderId) {
        log.info("Calculating total cost for order: {}", orderId);
        Order order = getOrder(orderId);

        OrderDto orderDto = orderMapper.toDto(order);
        BigDecimal productCost = paymentFeignClient.productCost(orderDto);
        BigDecimal totalCost = paymentFeignClient.getTotalCost(orderDto);

        order.setProductPrice(productCost);
        order.setTotalPrice(totalCost);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryCost(UUID orderId) {
        log.info("Calculating delivery cost for order: {}", orderId);
        Order order = getOrder(orderId);

        OrderDto orderDto = orderMapper.toDto(order);
        BigDecimal deliveryCost = deliveryFeignClient.deliveryCost(orderDto);

        order.setDeliveryPrice(deliveryCost);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assembly(UUID orderId) {
        log.info("Starting assembly for order: {}", orderId);
        Order order = getOrder(orderId);

        AssemblyProductsForOrderRequest request = AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(order.getProducts())
                .build();

        BookedProductsDto booked = warehouseFeignClient.assemblyProductForOrderFromShoppingCart(request);

        order.setState(OrderState.ASSEMBLED);
        order.setDeliveryWeight(booked.getDeliveryWeight());
        order.setDeliveryVolume(booked.getDeliveryVolume());
        order.setFragile(booked.getFragile());

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto assemblyFailed(UUID orderId) {
        log.info("Assembly failed for order: {}", orderId);
        Order order = getOrder(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderDto returnOrder(ProductReturnRequest request) {
        log.info("Processing return for order: {}", request.getOrderId());
        orderValidationService.validateProductReturnRequest(request);
        Order order = getOrder(request.getOrderId());

        warehouseFeignClient.acceptReturn(request.getProducts());

        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }
}
