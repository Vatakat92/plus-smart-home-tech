package ru.yandex.practicum.commerce.warehouse.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.warehouse.exception.ProductNotFoundOnWarehouseException;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class WarehouseValidationServiceImpl implements WarehouseValidationService {

    @Override
    public void validateNewProduct(NewProductInWarehouseRequest request) {
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (request.getWeight() != null && request.getWeight() <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
        if (request.getDimension() != null) {
            if (request.getDimension().getWidth() != null && request.getDimension().getWidth() <= 0 ||
                    request.getDimension().getHeight() != null && request.getDimension().getHeight() <= 0 ||
                    request.getDimension().getDepth() != null && request.getDimension().getDepth() <= 0) {
                throw new IllegalArgumentException("Dimensions must be positive");
            }
        }
    }

    @Override
    public void validateProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
    }

    @Override
    public void validateAddQuantity(AddProductToWarehouseRequest request) {
        validateProductId(request.getProductId());
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    @Override
    public void validateCart(ShoppingCartDto cart) {
        if (cart == null || cart.getProducts() == null || cart.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty or null");
        }
        for (Map.Entry<UUID, Integer> entry : cart.getProducts().entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException("Product ID in cart cannot be null");
            }
            if (entry.getValue() == null || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Product quantity must be positive for " + entry.getKey());
            }
        }
    }
}
