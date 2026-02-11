package ru.yandex.practicum.commerce.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.enums.ProductCategory;
import ru.yandex.practicum.commerce.enums.ProductState;
import ru.yandex.practicum.commerce.enums.QuantityState;
import ru.yandex.practicum.commerce.store.entity.ProductEntity;
import ru.yandex.practicum.commerce.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;
import ru.yandex.practicum.commerce.store.validation.StoreValidationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StoreValidationService validationService;

    public List<ProductDto> getProducts(
            String category,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String sort) {

        Sort sortSpec = buildSort(sortBy, sortDir, sort);
        Pageable pageable = PageRequest.of(page, size, sortSpec);

        Page<ProductEntity> pageResult;
        if (category != null) {
            // Если category=CONTROL, возвращаем все продукты без фильтрации по состоянию
            if ("CONTROL".equals(category)) {
                pageResult = productRepository.findAll(pageable);
            } else {
                ProductCategory cat = parseCategory(category);
                pageResult = productRepository.findByProductCategoryAndProductState(cat, ProductState.ACTIVE, pageable);
            }
        } else {
            pageResult = productRepository.findByProductState(ProductState.ACTIVE, pageable);
        }

        return pageResult.map(productMapper::toDto).getContent();
    }

    public String getActualSortDirection(String sortDir, String sort) {
        String dir = validationService.validateSortDirection(sortDir);
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            if (parts.length > 1) {
                dir = validationService.validateSortDirection(parts[1]);
            }
        }
        return dir;
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        ProductEntity entity = productMapper.toEntity(dto);

        // Используем переданный productState или ACTIVE по умолчанию
        if (dto.getProductState() == null) {
            entity.setProductState(ProductState.ACTIVE);
            log.info("Product state not provided, setting to ACTIVE");
        } else {
            log.info("Product state provided: {}", dto.getProductState());
        }

        entity.setQuantityState(dto.getQuantityState() != null ? dto.getQuantityState() : QuantityState.ENOUGH);
        return productMapper.toDto(productRepository.save(entity));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto dto) {
        ProductEntity existing = getEntity(dto.getProductId());
        ProductEntity updated = productMapper.toEntity(dto);
        updated.setProductState(existing.getProductState());
        updated.setQuantityState(existing.getQuantityState());
        return productMapper.toDto(productRepository.save(updated));
    }

    @Transactional
    public ProductDto deactivateProduct(UUID productId) {
        ProductEntity product = getEntity(productId);
        product.setProductState(ProductState.DEACTIVATE);
        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDto updateQuantityState(UUID productId, String quantityState) {
        QuantityState state = parseQuantityState(quantityState);
        ProductEntity product = getEntity(productId);
        product.setQuantityState(state);
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto getProductById(UUID productId) {
        return productMapper.toDto(getEntity(productId));
    }

    private ProductEntity getEntity(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    private ProductCategory parseCategory(String value) {
        try {
            return ProductCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category: " + value);
        }
    }

    private QuantityState parseQuantityState(String value) {
        try {
            return QuantityState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid quantity state: " + value);
        }
    }

    private Sort buildSort(String sortBy, String sortDir, String sort) {
        String field = validationService.validateSortField(sortBy);
        String dir = validationService.validateSortDirection(sortDir);

        // Параметр sort имеет приоритет над sortBy/sortDir
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            field = validationService.validateSortField(parts[0]);
            if (parts.length > 1) {
                dir = validationService.validateSortDirection(parts[1]);
            }
        }

        return Sort.by(Sort.Direction.fromString(dir), field);
    }
}
