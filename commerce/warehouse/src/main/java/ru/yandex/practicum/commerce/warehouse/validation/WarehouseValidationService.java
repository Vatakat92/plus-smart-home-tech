package ru.yandex.practicum.commerce.warehouse.validation;

import ru.yandex.practicum.commerce.dto.*;

import java.util.UUID;

public interface WarehouseValidationService {
    void validateNewProduct(NewProductInWarehouseRequest request);

    void validateProductId(UUID productId);

    void validateAddQuantity(AddProductToWarehouseRequest request);

    void validateCart(ShoppingCartDto cart);
}
