package ru.yandex.practicum.commerce.contract.shopping.cart;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartNotInWarehouseException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartLowQuantityInWarehouseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartOperations {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getShoppingCart(@RequestParam String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductToShoppingCart(
            @RequestParam String username,
            @Valid @RequestBody @NotNull @NotEmpty Map<UUID, Integer> products)
            throws ProductInShoppingCartNotInWarehouseException,
                   ProductInShoppingCartLowQuantityInWarehouseException;

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeFromShoppingCart(
            @RequestParam String username,
            @Valid @RequestBody @NotNull @NotEmpty List<UUID> productIds)
            throws NoProductsInShoppingCartException;

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam String username,
            @Valid @RequestBody @NotNull ChangeProductQuantityRequest changeProductQuantityRequest)
            throws NoProductsInShoppingCartException,
                   ProductInShoppingCartNotInWarehouseException,
                   ProductInShoppingCartLowQuantityInWarehouseException;

    @DeleteMapping("/api/v1/shopping-cart")
    void deactivateCurrentShoppingCart(@RequestParam String username);
}