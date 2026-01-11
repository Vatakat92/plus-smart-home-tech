package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", url = "${feign.client.shopping-cart.url:}")
public interface ShoppingCartFeignClient {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getUserShoppingCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductToCart(@RequestParam("username") String username,
                                     @RequestBody Map<UUID, Integer> productQuantities);

    @DeleteMapping("/api/v1/shopping-cart")
    void deactivateCart(@RequestParam("username") String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam("username") String username,
                                           @RequestBody List<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam("username") String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}