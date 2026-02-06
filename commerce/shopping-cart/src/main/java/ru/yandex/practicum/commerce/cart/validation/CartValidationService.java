package ru.yandex.practicum.commerce.cart.validation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartValidationService {

    void validateUsername(String username);

    void validateProducts(Map<UUID, Integer> products);

    void validateProductIds(List<UUID> productIds);
}
