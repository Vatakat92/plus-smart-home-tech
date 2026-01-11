package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreFeignClient {
    
    @GetMapping("/api/v1/shopping-store")
    ResponseEntity<List<ProductDto>> getProductsByCategory(@RequestParam(required = false) String category, 
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size);

    @PutMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ResponseEntity<Boolean> removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    ResponseEntity<Boolean> setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("productId") UUID productId);
}