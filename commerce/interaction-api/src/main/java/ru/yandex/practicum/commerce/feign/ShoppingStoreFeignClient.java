package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreFeignClient {
    
    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> getProductsByCategory(@RequestParam(required = false) String category, 
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size);

    @PutMapping("/api/v1/shopping-store")
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    Boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    Boolean setProductQuantityState(@RequestBody SetProductQuantityStateRequest request);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable("productId") UUID productId);
}