package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ProductDto>> getProductsByCategory(String category, int page, int size) {
        try {
            List<ProductDto> products = productService.getProductsByCategory(category, page, size);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> createProduct(@Valid ProductDto productDto) {
        try {
            ProductDto createdProduct = productService.createProduct(productDto);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(@Valid ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(productDto);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Boolean> removeProductFromStore(UUID productId) {
        try {
            Boolean result = productService.removeProductFromStore(productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Boolean> setProductQuantityState(@Valid SetProductQuantityStateRequest request) {
        try {
            Boolean result = productService.setProductQuantityState(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> getProductById(UUID productId) {
        try {
            ProductDto product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}