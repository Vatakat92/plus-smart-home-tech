package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.dto.SortedContentResponseDto;
import ru.yandex.practicum.commerce.store.service.ProductService;
import ru.yandex.practicum.commerce.store.validation.StoreValidationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class ShoppingStoreController implements ShoppingStoreOperations {

    private final ProductService productService;
    private final StoreValidationService validationService;

    @Override
    public SortedContentResponseDto<ProductDto> getProductsByCategory(
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String sort) {

        int validSize = validationService.validatePageSize(size);
        String validSortBy = validationService.validateSortField(sortBy);
        String validSortDir = validationService.validateSortDirection(sortDir);

        log.info("Get products: category={}, page={}, size={}, sortBy={}, sortDir={}",
                category, page, validSize, validSortBy, validSortDir);

        List<ProductDto> products =
                productService.getProducts(category, page, validSize, validSortBy, validSortDir, sort);

        // Получаем фактически применённое направление сортировки
        String actualSortDir = productService.getActualSortDirection(validSortDir, sort);

        SortedContentResponseDto.SortInfo sortInfo = new SortedContentResponseDto.SortInfo(validSortBy, actualSortDir);

        return SortedContentResponseDto.of(products, List.of(sortInfo));
    }

    @Override
    public ProductDto createProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Create product {}", productDto);
        return productService.createProduct(productDto);
    }

    @Override
    public ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("Update product {}", productDto.getProductId());
        return productService.updateProduct(productDto);
    }

    @Override
    public ProductDto removeProductFromStore(@RequestBody @NotNull UUID productId) {
        log.info("Deactivate product {}", productId);
        return productService.deactivateProduct(productId);
    }

    @Override
    public ProductDto setProductQuantityState(@RequestParam @NotNull String productId, @RequestParam @NotNull String quantityState) {
        UUID id = validationService.validateAndConvertProductId(productId);
        return productService.updateQuantityState(id, quantityState); // теперь сервис принимает UUID
    }

    @Override
    public ProductDto getProductById(@NotNull UUID productId) {
        return productService.getProductById(productId);
    }
}
