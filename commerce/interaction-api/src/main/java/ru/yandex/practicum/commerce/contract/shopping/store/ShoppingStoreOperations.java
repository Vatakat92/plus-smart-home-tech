package ru.yandex.practicum.commerce.contract.shopping.store;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.dto.SortedContentResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface ShoppingStoreOperations {

    @GetMapping("/api/v1/shopping-store")
    SortedContentResponseDto<ProductDto> getProductsByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort);

    @PutMapping("/api/v1/shopping-store")
    ProductDto createProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    ProductDto removeProductFromStore(@RequestBody @NotNull UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    ProductDto setProductQuantityState(
            @RequestParam @NotNull String productId,
            @RequestParam @NotNull String quantityState);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable("productId") @NotNull UUID productId);
}