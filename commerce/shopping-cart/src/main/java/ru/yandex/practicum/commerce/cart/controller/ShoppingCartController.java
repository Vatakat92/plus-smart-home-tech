package ru.yandex.practicum.commerce.cart.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;

import ru.yandex.practicum.commerce.cart.service.ShoppingCartService;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperations;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartNotInWarehouseException;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        log.info("Get shopping cart for user {}", username);
        return shoppingCartService.getShoppingCart(username);
    }

    @Override
    public ShoppingCartDto addProductToShoppingCart(
            String username,
            Map<UUID, Integer> products)
            throws ProductInShoppingCartNotInWarehouseException,
            ProductInShoppingCartLowQuantityInWarehouseException {

        log.info("Add products {} to shopping cart for user {}", products, username);
        return shoppingCartService.addProductToShoppingCart(username, products);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(
            String username,
            List<UUID> productIds)
            throws NoProductsInShoppingCartException {

        log.info("Remove products {} from shopping cart for user {}", productIds, username);
        return shoppingCartService.removeProductsFromCart(username, productIds);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(
            String username,
            ChangeProductQuantityRequest changeProductQuantityRequest)
            throws NoProductsInShoppingCartException,
            ProductInShoppingCartNotInWarehouseException,
            ProductInShoppingCartLowQuantityInWarehouseException {

        log.info("Change quantity for product {} to {} in shopping cart for user {}",
                changeProductQuantityRequest.getProductId(),
                changeProductQuantityRequest.getNewQuantity(),
                username);
        return shoppingCartService.changeProductQuantity(username, changeProductQuantityRequest);
    }

    @Override
    public void deactivateCurrentShoppingCart(String username) {
        log.info("Deactivate shopping cart for user {}", username);
        shoppingCartService.deactivateCart(username);
    }
}
