package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.dto.SortedContentResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


@FeignClient(name = "shopping-store")
public interface ShoppingStoreFeignClient {
    
    @GetMapping("/api/v1/shopping-store")
    ResponseEntity<SortedContentResponseDto<ProductDto>> getProductsByCategory(@RequestParam(required = false) String category, 
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(required = false) String sortBy,
                                          @RequestParam(required = false, defaultValue = "ASC") String sortDir,
                                          @RequestParam(required = false) String sort);

    @PutMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ResponseEntity<ProductDto> removeProductFromStore(@RequestBody @NotNull UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    ResponseEntity<ProductDto> setProductQuantityState(@RequestParam @NotNull String productId, @RequestParam @NotNull String quantityState);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ResponseEntity<ProductDto> getProductById(@PathVariable("productId") @NotNull UUID productId);
}