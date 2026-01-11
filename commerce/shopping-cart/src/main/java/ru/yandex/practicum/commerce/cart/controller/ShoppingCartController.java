package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
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
    public ShoppingCartDto getUserShoppingCart(String username) {
        return shoppingCartService.getUserShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToCart(String username, @Valid Map<UUID, Integer> productQuantities) {
        return shoppingCartService.addProductToCart(username, productQuantities);
    }

    @Override
    public void deactivateCart(String username) {
        shoppingCartService.deactivateCart(username);
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, @Valid List<UUID> productIds) {
        return shoppingCartService.removeProductsFromCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String username, @Valid ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(username, request);
    }
}