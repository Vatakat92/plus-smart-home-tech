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
import ru.yandex.practicum.commerce.dto.ChangeProductQuantityRequest;
import ru.yandex.practicum.commerce.dto.ShoppingCartDto;
import ru.yandex.practicum.commerce.feign.WarehouseFeignClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final WarehouseFeignClient warehouseFeignClient;
    private final ShoppingCartMapper shoppingCartMapper;

    @Transactional(readOnly = true)
    public ShoppingCartDto getUserShoppingCart(String username) {
        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> {
                    ShoppingCartEntity cartEntity = new ShoppingCartEntity();
                    cartEntity.setUsername(username);
                    cartEntity.setProducts(new HashMap<>());
                    return shoppingCartMapper.toDto(shoppingCartRepository.save(cartEntity));
                });
    }

    @CircuitBreaker(name = "warehouse", fallbackMethod = "addProductToCartFallback")
    @Transactional
    public ShoppingCartDto addProductToCart(String username, Map<UUID, Integer> productQuantities) {
        ShoppingCartEntity cartEntity = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setUsername(username);
                    newCart.setProducts(new HashMap<>());
                    return shoppingCartRepository.save(newCart);
                });

        ShoppingCartDto tempCart = new ShoppingCartDto();
        Map<UUID, Integer> tempProducts = new HashMap<>(cartEntity.getProducts());

        productQuantities.forEach((productId, quantity) ->
                tempProducts.merge(productId, quantity, Integer::sum)
        );

        tempCart.setProducts(tempProducts);

        warehouseFeignClient.checkAvailability(tempCart);

        productQuantities.forEach((productId, quantity) ->
                cartEntity.getProducts().merge(productId, quantity, Integer::sum)
        );

        ShoppingCartEntity savedCart = shoppingCartRepository.save(cartEntity);
        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public void deactivateCart(String username) {
        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found for user: " + username));
        cart.setIsActive(false);
        shoppingCartRepository.save(cart);
    }

    @Transactional
    public ShoppingCartDto removeProductsFromCart(String username, List<String> productIds) {
        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseThrow(() -> new ShoppingCartNotFoundException("Cart not found for user: " + username));

        List<UUID> uuidList = productIds.stream()
            .map(UUID::fromString)
            .toList();
        
        uuidList.forEach(productId -> cart.getProducts().remove(productId));

        ShoppingCartEntity savedCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(savedCart);
    }

    @Transactional
    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCartEntity cart = shoppingCartRepository.findByUsername(username)
                .orElseGet(() -> {
                    ShoppingCartEntity newCart = new ShoppingCartEntity();
                    newCart.setUsername(username);
                    newCart.setProducts(new HashMap<>());
                    return shoppingCartRepository.save(newCart);
                });

        if (cart.getProducts().containsKey(request.getProductId())) {
            if (request.getNewQuantity() <= 0) {
                cart.getProducts().remove(request.getProductId());
            } else {
                cart.getProducts().put(request.getProductId(), request.getNewQuantity());
            }
        }

        ShoppingCartEntity savedCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.toDto(savedCart);
    }
    
    public ShoppingCartDto addProductToCartFallback(String username, Map<UUID, Integer> productQuantities, Throwable ex) {
        log.error("Ошибка при добавлении товаров в корзину. Сервис склада недоступен: {}. Запрошено добавление продуктов: {}", ex.getMessage(), productQuantities);
        return shoppingCartRepository.findByUsername(username)
                .map(shoppingCartMapper::toDto)
                .orElseGet(() -> {
                    ShoppingCartEntity cartEntity = new ShoppingCartEntity();
                    cartEntity.setUsername(username);
                    cartEntity.setProducts(new HashMap<>());
                    return shoppingCartMapper.toDto(shoppingCartRepository.save(cartEntity));
                });
    }


}