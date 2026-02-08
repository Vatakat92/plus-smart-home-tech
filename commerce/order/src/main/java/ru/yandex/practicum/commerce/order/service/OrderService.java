package ru.yandex.practicum.commerce.order.service;

import ru.yandex.practicum.commerce.dto.*;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto createNewOrder(CreateNewOrderRequest request);
    List<OrderDto> getClientOrders(String username);
    OrderDto payment(UUID orderId);
    OrderDto paymentFailed(UUID orderId);
    OrderDto delivery(UUID orderId);
    OrderDto deliveryFailed(UUID orderId);
    OrderDto completed(UUID orderId);
    OrderDto calculateTotalCost(UUID orderId);
    OrderDto calculateDeliveryCost(UUID orderId);
    OrderDto assembly(UUID orderId);
    OrderDto assemblyFailed(UUID orderId);
    OrderDto returnOrder(ProductReturnRequest request);
}
