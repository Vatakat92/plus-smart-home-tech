package ru.yandex.practicum.commerce.contract.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.OrderDto;
import ru.yandex.practicum.commerce.dto.ProductReturnRequest;

import java.util.List;
import java.util.UUID;

public interface OrderOperations {

    @PutMapping("/api/v1/order")
    OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest request);

    @GetMapping("/api/v1/order/{username}")
    List<OrderDto> getClientOrders(@PathVariable String username);

    @PostMapping("/api/v1/order/payment")
    OrderDto payment(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto paymentFailed(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery")
    OrderDto delivery(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto deliveryFailed(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/completed")
    OrderDto completed(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    OrderDto calculateTotalCost(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    OrderDto calculateDeliveryCost(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly")
    OrderDto assembly(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto assemblyFailed(@Valid @RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/return")
    OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest request);
}
