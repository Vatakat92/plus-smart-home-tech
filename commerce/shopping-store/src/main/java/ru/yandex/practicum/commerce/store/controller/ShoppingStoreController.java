package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.commerce.store.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    
    private final ProductService productService;
    
    @Override
    public List<ProductDto> getProductsByCategory(String category, int page, int size) {
        return productService.getProductsByCategory(category, page, size);
    }

    @Override
    public ProductDto createProduct(@Valid ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@Valid ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    @Override
    public Boolean removeProductFromStore(UUID productId) {
        return productService.removeProductFromStore(productId);
    }

    @Override
    public Boolean setProductQuantityState(@Valid SetProductQuantityStateRequest request) {
        return productService.setProductQuantityState(request);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return productService.getProductById(productId);
    }
}