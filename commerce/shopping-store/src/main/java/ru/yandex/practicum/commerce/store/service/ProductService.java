package ru.yandex.practicum.commerce.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.dto.SetProductQuantityStateRequest;
import ru.yandex.practicum.commerce.enums.ProductCategory;
import ru.yandex.practicum.commerce.enums.ProductState;
import ru.yandex.practicum.commerce.enums.QuantityState;
import ru.yandex.practicum.commerce.store.entity.ProductEntity;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.store.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByCategory(String categoryStr, int page, int size) {
        Optional<ProductCategory> category = Optional.ofNullable(categoryStr)
                .filter(cat -> !cat.isEmpty())
                .map(cat -> ProductCategory.valueOf(cat.toUpperCase()));

        Pageable pageable = PageRequest.of(page, size);

        List<ProductEntity> entities = category
                .map(cat -> productRepository.findByProductCategoryAndProductState(cat, ProductState.ACTIVE, pageable).getContent())
                .orElseGet(() -> productRepository.findByProductState(ProductState.ACTIVE, pageable).getContent());

        return entities.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        ProductEntity entity = productMapper.toEntity(productDto);
        entity.setProductState(ProductState.ACTIVE);
        entity.setQuantityState(QuantityState.ENOUGH); // по умолчанию

        ProductEntity savedEntity = productRepository.save(entity);
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
        return productMapper.toDto(updatedEntity);
    }

    @Transactional
    public boolean removeProductFromStore(UUID productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setProductState(ProductState.DEACTIVATE);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean setProductQuantityState(SetProductQuantityStateRequest request) {
        return productRepository.findById(request.getProductId())
                .map(product -> {
                    QuantityState state = QuantityState.valueOf(request.getQuantityState().toUpperCase());
                    product.setQuantityState(state);
                    productRepository.save(product);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        ProductEntity entity = productRepository.findByProductIdAndProductState(productId, ProductState.ACTIVE);

        if (entity == null) {
            throw new RuntimeException("Product not found");
        }

        return productMapper.toDto(entity);
    }


}