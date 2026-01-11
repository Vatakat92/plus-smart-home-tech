package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", url = "${feign.client.shopping-cart.url:}")
public interface ShoppingCartFeignClient {

    @GetMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> getUserShoppingCart(@RequestParam("username") String username);

    @PutMapping("/api/v1/shopping-cart")
    ResponseEntity<ShoppingCartDto> addProductToCart(@RequestParam("username") String username,
                                     @RequestBody Map<UUID, Integer> productQuantities);

    @DeleteMapping("/api/v1/shopping-cart")
    ResponseEntity<Void> deactivateCart(@RequestParam("username") String username);

    @PostMapping("/api/v1/shopping-cart/remove")
    ResponseEntity<ShoppingCartDto> removeProductsFromCart(@RequestParam("username") String username,
                                           @RequestBody List<UUID> productIds);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ResponseEntity<ShoppingCartDto> changeProductQuantity(@RequestParam("username") String username,
                                          @RequestBody ChangeProductQuantityRequest request);
}