package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class ShoppingCartController implements ShoppingCartFeignClient {
    
    private final ShoppingCartService shoppingCartService;
    
    @Override
    public ResponseEntity<ShoppingCartDto> getUserShoppingCart(String username) {
        try {
            ShoppingCartDto cart = shoppingCartService.getUserShoppingCart(username);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error getting user shopping cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> addProductToCart(String username, Map<UUID, Integer> productQuantities) {
        try {
            ShoppingCartDto cart = shoppingCartService.addProductToCart(username, productQuantities);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error adding product to cart for user {}: {}", username, e.getMessage());
            // Возвращаем fallback корзину при ошибке
            ShoppingCartDto fallbackCart = new ShoppingCartDto();
            fallbackCart.setProducts(new java.util.HashMap<>());
            return ResponseEntity.ok(fallbackCart);
        }
    }

    @Override
    public ResponseEntity<Void> deactivateCart(String username) {
        try {
            shoppingCartService.deactivateCart(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deactivating cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> removeProductsFromCart(String username, List<String> productIds) {
        try {
            ShoppingCartDto cart = shoppingCartService.removeProductsFromCart(username, productIds);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error removing products from cart for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        try {
            ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            log.error("Error changing product quantity for user {}: {}", username, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}