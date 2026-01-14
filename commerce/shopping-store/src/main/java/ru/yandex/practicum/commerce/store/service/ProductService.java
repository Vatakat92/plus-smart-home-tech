package ru.yandex.practicum.commerce.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    


    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String categoryStr, int page, int size, String sortBy, String sortDir) {
        Optional<ProductCategory> category = Optional.ofNullable(categoryStr)
                .filter(cat -> !cat.isEmpty())
                .map(cat -> ProductCategory.valueOf(cat.toUpperCase()));

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "ASC"), 
                            sortBy != null ? sortBy : "productName");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductEntity> entityPage = category
                .map(cat -> productRepository.findByProductCategoryAndProductState(cat, ProductState.ACTIVE, pageable))
                .orElseGet(() -> productRepository.findByProductState(ProductState.ACTIVE, pageable));

        return entityPage.getContent().stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ProductEntity> getProductsPage(String categoryStr, int page, int size, String sortBy, String sortDir) {
        Optional<ProductCategory> category = Optional.ofNullable(categoryStr)
                .filter(cat -> !cat.isEmpty())
                .map(cat -> ProductCategory.valueOf(cat.toUpperCase()));

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir != null ? sortDir : "ASC"), 
                            sortBy != null ? sortBy : "productName");
        Pageable pageable = PageRequest.of(page, size, sort);

        return category
                .map(cat -> productRepository.findByProductCategoryAndProductState(cat, ProductState.ACTIVE, pageable))
                .orElseGet(() -> productRepository.findByProductState(ProductState.ACTIVE, pageable));
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity entity = productMapper.toEntity(productDto);
        // При добавлении продукта в магазин, он должен быть в активном состоянии
        entity.setProductState(ProductState.ACTIVE);
        // Установка quantityState из DTO, а не по умолчанию
        if (productDto.getQuantityState() != null) {
            entity.setQuantityState(productDto.getQuantityState());
        } else {
            entity.setQuantityState(QuantityState.ENOUGH); // по умолчанию
        }

        ProductEntity savedEntity = productRepository.save(entity);
        log.info("Created product {} with state {}", savedEntity.getProductId(), savedEntity.getProductState());
        return productMapper.toDto(savedEntity);
    }

    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        ProductEntity existing = productRepository.findById(productDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductEntity updatedEntity = productMapper.toEntity(productDto);
        updatedEntity.setProductState(existing.getProductState());
        updatedEntity.setQuantityState(existing.getQuantityState());
        updatedEntity = productRepository.save(updatedEntity);
        log.info("Updated product {} to state {}", updatedEntity.getProductId(), updatedEntity.getProductState());
        return productMapper.toDto(updatedEntity);
    }

    @Transactional
    public ProductDto removeProductFromStoreWithResult(UUID productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setProductState(ProductState.DEACTIVATE);
        ProductEntity updatedProduct = productRepository.save(product);
        log.info("Removed product {} by setting state to {}", updatedProduct.getProductId(), updatedProduct.getProductState());
        return productMapper.toDto(updatedProduct);
    }



    @Transactional
    public ProductDto setProductQuantityState(UUID productId, String quantityState) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        QuantityState state = QuantityState.valueOf(quantityState.toUpperCase());
        product.setQuantityState(state);
        ProductEntity updatedProduct = productRepository.save(product);
        log.info("Set quantity state for product {} to {}, product state remains {}", 
                 updatedProduct.getProductId(), updatedProduct.getQuantityState(), updatedProduct.getProductState());
        return productMapper.toDto(updatedProduct);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        // Получаем товар независимо от его состояния
        ProductEntity entity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        return productMapper.toDto(entity);
    }


}