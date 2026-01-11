package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.feign.ShoppingCartFeignClient;
import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> addProductToCart(String username, @Valid Map<UUID, Integer> productQuantities) {
        try {
            ShoppingCartDto cart = shoppingCartService.addProductToCart(username, productQuantities);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> deactivateCart(String username) {
        try {
            shoppingCartService.deactivateCart(username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> removeProductsFromCart(String username, @Valid List<UUID> productIds) {
        try {
            ShoppingCartDto cart = shoppingCartService.removeProductsFromCart(username, productIds);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ShoppingCartDto> changeProductQuantity(String username, @Valid ChangeProductQuantityRequest request) {
        try {
            ShoppingCartDto cart = shoppingCartService.changeProductQuantity(username, request);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}