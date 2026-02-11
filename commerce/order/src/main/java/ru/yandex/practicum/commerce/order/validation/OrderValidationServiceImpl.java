package ru.yandex.practicum.commerce.order.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.CreateNewOrderRequest;
import ru.yandex.practicum.commerce.dto.ProductReturnRequest;
import ru.yandex.practicum.commerce.order.exception.InvalidOrderException;

import java.util.UUID;

@Slf4j
@Service
public class OrderValidationServiceImpl implements OrderValidationService {

    @Override
    public void validateShoppingCartId(UUID shoppingCartId) {
        if (shoppingCartId == null) {
            throw new InvalidOrderException("Shopping cart ID cannot be null");
        }
    }

    @Override
    public void validateCreateOrderRequest(CreateNewOrderRequest request) {
        if (request == null) {
            throw new InvalidOrderException("Create order request cannot be null");
        }
        validateShoppingCartId(request.getShoppingCartId());
    }

    @Override
    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidOrderException("Username cannot be null or empty");
        }
    }

    @Override
    public void validateProductReturnRequest(ProductReturnRequest request) {
        if (request == null) {
            throw new InvalidOrderException("Product return request cannot be null");
        }
        if (request.getOrderId() == null) {
            throw new InvalidOrderException("Order ID in return request cannot be null");
        }
        if (request.getProducts() == null || request.getProducts().isEmpty()) {
            throw new InvalidOrderException("Products to return cannot be null or empty");
        }
    }
}
