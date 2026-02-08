package ru.yandex.practicum.commerce.cart.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.cart.entity.ShoppingCartEntity;
import ru.yandex.practicum.commerce.cart.exception.ShoppingCartNotFoundException;
import ru.yandex.practicum.commerce.cart.mapper.ShoppingCartMapper;
import ru.yandex.practicum.commerce.cart.repository.ShoppingCartRepository;
import ru.yandex.practicum.commerce.cart.validation.CartValidationService;
import ru.yandex.practicum.commerce.feign.WarehouseFeignClient;
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.commerce.contract.shopping.cart.exception.ProductInShoppingCartNotInWarehouseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseFeignClient warehouseFeignClient;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartValidationService cartValidationService;

    public ShoppingCartDto getShoppingCart(String username) {
        cartValidationService.validateUsername(username);

        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setUsername(username);
                    newCart.setProducts(new HashMap<>());
                    return shoppingCartMapper.toDto(shoppingCartRepository.save(newCart));
                });
    }

    public ShoppingCartDto getShoppingCartById(String shoppingCartId) {
        UUID cartId = UUID.fromString(shoppingCartId);
        return shoppingCartRepository.findById(cartId)
                .map(shoppingCartMapper::toDto)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found: " + shoppingCartId));
    }

    @CircuitBreaker(name = "warehouse", fallbackMethod = "addProductToCartFallback")
    @Transactional
    public ShoppingCartDto addProductToShoppingCart(String username, Map<UUID, Long> productQuantities) {

        cartValidationService.validateUsername(username);
        cartValidationService.validateProducts(productQuantities);

        productQuantities.forEach((id, qty) -> {
            if (qty <= 0) throw new IllegalArgumentException("Product quantity must be positive: " + id);
        });

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setUsername(username);
                    newCart.setProducts(new HashMap<>());
                    return shoppingCartRepository.save(newCart);
                });

        Map<UUID, Long> tempProducts = new HashMap<>(cart.getProducts());
        productQuantities.forEach((productId, quantity) -> tempProducts.merge(productId, quantity, Long::sum));
        ShoppingCartDto tempCart = new ShoppingCartDto();
        tempCart.setProducts(tempProducts);

        try {
            warehouseFeignClient.checkAvailability(tempCart);
        } catch (ProductInShoppingCartNotInWarehouseException | ProductInShoppingCartLowQuantityInWarehouseException e) {
            throw e;
        } catch (Exception e) {
            throw new ProductInShoppingCartNotInWarehouseException("Failed to check availability: " + e.getMessage());
        }

        productQuantities.forEach((productId, quantity) -> cart.getProducts().merge(productId, quantity, Long::sum));
        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Transactional
    public void deactivateCart(String username) {
        cartValidationService.validateUsername(username);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found for user: " + username));
        cart.setIsActive(false);
        shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productIds)
            throws NoProductsInShoppingCartException {

        cartValidationService.validateUsername(username);
        cartValidationService.validateProductIds(productIds);

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found for user: " + username));

        boolean anyRemoved = false;
        for (UUID productId : productIds) {
            if (cart.getProducts().containsKey(productId)) {
                cart.getProducts().remove(productId);
                anyRemoved = true;
            }
        }

        if (!anyRemoved) throw new NoProductsInShoppingCartException("No matching products found in cart");

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request)
            throws NoProductsInShoppingCartException {

        cartValidationService.validateUsername(username);

        if (request.getProductId() == null) throw new IllegalArgumentException("Product ID cannot be null");
        if (request.getNewQuantity() < 0) throw new IllegalArgumentException("New quantity cannot be negative");

        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found for user: " + username));

        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Product not found in cart");
        }

        if (request.getNewQuantity() <= 0) {
            cart.getProducts().remove(request.getProductId());
        } else {
            Map<UUID, Long> tempProducts = new HashMap<>();
            tempProducts.put(request.getProductId(), request.getNewQuantity());
            ShoppingCartDto tempCart = new ShoppingCartDto();
            tempCart.setProducts(tempProducts);

            try {
                warehouseFeignClient.checkAvailability(tempCart);
            } catch (ProductInShoppingCartNotInWarehouseException | ProductInShoppingCartLowQuantityInWarehouseException e) {
                throw e;
            } catch (Exception e) {
                throw new ProductInShoppingCartNotInWarehouseException("Failed to check availability: " + e.getMessage());
            }

            cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        }

        return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
    }

    public ShoppingCartDto addProductToCartFallback(String username, Map<UUID, Long> productQuantities, Throwable ex) {
        log.error("Ошибка при добавлении товаров в корзину. Сервис склада недоступен: {}. Запрошено добавление продуктов: {}",
                ex.getMessage(), productQuantities);

        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> {
                    ShoppingCartEntity cart = new ShoppingCartEntity();
                    cart.setUsername(username);
                    cart.setProducts(new HashMap<>());
                    return shoppingCartMapper.toDto(shoppingCartRepository.save(cart));
                });
    }
}
