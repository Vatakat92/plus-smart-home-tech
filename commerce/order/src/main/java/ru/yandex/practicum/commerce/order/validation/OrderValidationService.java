package ru.yandex.practicum.commerce.order.validation;

import ru.yandex.practicum.commerce.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.ProductReturnRequest;

import java.util.UUID;

public interface OrderValidationService {
    void validateShoppingCartId(UUID shoppingCartId);
    void validateCreateOrderRequest(CreateNewOrderRequest request);
    void validateUsername(String username);
    void validateProductReturnRequest(ProductReturnRequest request);
}
