package ru.yandex.practicum.commerce.cart.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.cart.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.cart.exception.ShoppingCartNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CartValidationServiceImpl implements CartValidationService {

    @Override
    public void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            log.warn("Username is null or blank");
            throw new ShoppingCartNotFoundException("Username must not be empty or cart not found");
        }
    }

    @Override
    public void validateProducts(Map<UUID, Long> products) {
        if (products == null || products.isEmpty()) {
            log.warn("Products map is empty");
            throw new ProductNotFoundException("Products list must not be empty");
        }
    }

    @Override
    public void validateProductIds(List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            log.warn("ProductIds list is empty");
            throw new ProductNotFoundException("ProductIds list must not be empty");
        }
    }

}
