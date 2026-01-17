package ru.yandex.practicum.commerce.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import ru.yandex.practicum.commerce.dto.ProductDto;
import ru.yandex.practicum.commerce.feign.ShoppingStoreFeignClient;
import ru.yandex.practicum.commerce.store.exception.ProductNotFoundException;
import ru.yandex.practicum.commerce.store.service.ProductService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.yandex.practicum.commerce.dto.PagedResponseDto;
import ru.yandex.practicum.commerce.dto.SortedContentResponseDto;
import ru.yandex.practicum.commerce.store.entity.ProductEntity;
import ru.yandex.practicum.commerce.store.mapper.ProductMapper;
import ru.yandex.practicum.commerce.validation.ValidationService;
import ru.yandex.practicum.commerce.exception.ResourceNotFoundException;




@Slf4j
@RestController
@RequiredArgsConstructor

@Validated
public class ShoppingStoreController implements ShoppingStoreFeignClient {
    
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ValidationService validationService;
    
    @Override
    @GetMapping("/api/v1/shopping-store")
    public ResponseEntity<SortedContentResponseDto<ProductDto>> getProductsByCategory(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort) {
        try {
            int maxSize = validationService.validatePageSize(size);
            
            String actualSortBy = sortBy;
            String actualSortDir = sortDir;
            
            // Если передан параметр sort в формате "field,direction", парсим его
            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length >= 1) {
                    actualSortBy = sortParts[0];
                }
                if (sortParts.length >= 2) {
                    actualSortDir = sortParts[1].toUpperCase();
                }
            }
            
            actualSortBy = validationService.validateSortField(actualSortBy);
            actualSortDir = validationService.validateSortDirection(actualSortDir);
            
            log.info("Getting products by category: {}, page: {}, size: {}, sortBy: {}, sortDir: {}", 
                     category, page, maxSize, actualSortBy, actualSortDir);
            List<ProductDto> products = productService.getProductsByCategory(category, page, maxSize, actualSortBy, actualSortDir);
            log.info("Retrieved {} products", products.size());

            String sortProperty = actualSortBy != null ? actualSortBy : "productName";
            String sortDirection = actualSortDir != null ? actualSortDir : "ASC";
            List<SortedContentResponseDto.SortInfo> sortInfoList = List.of(
                new SortedContentResponseDto.SortInfo(sortProperty, sortDirection)
            );
            
            SortedContentResponseDto<ProductDto> response = SortedContentResponseDto.of(products, sortInfoList);
            log.info("Returning response with content size: {}", response.getContent().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting products by category: {}", e.getMessage());
            List<SortedContentResponseDto.SortInfo> sortInfoList = List.of(
                new SortedContentResponseDto.SortInfo("productName", "DESC")
            );
            SortedContentResponseDto<ProductDto> errorResponse = SortedContentResponseDto.of(List.of(), sortInfoList);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/api/v1/shopping-store/with-content", params = {"!category"})
    public ResponseEntity<SortedContentResponseDto<ProductDto>> getAllProductsWithContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort) {
        try {
            int maxSize = validationService.validatePageSize(size);
            return getProductsByCategoryWithContent(null, page, maxSize, sortBy, sortDir, sort);
        } catch (Exception e) {
            log.error("Error getting all products with content: {}", e.getMessage());
            String actualSortBy = validationService.validateSortField(sortBy);
            String actualSortDir = validationService.validateSortDirection(sortDir);
            
            // Если передан параметр sort в формате "field,direction", парсим его
            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length >= 1) {
                    actualSortBy = sortParts[0];
                }
                if (sortParts.length >= 2) {
                    actualSortDir = sortParts[1].toUpperCase();
                }
            }
            
            actualSortBy = validationService.validateSortField(actualSortBy);
            actualSortDir = validationService.validateSortDirection(actualSortDir);
            
            String sortProperty = actualSortBy != null ? actualSortBy : "productName";
            String sortDirection = actualSortDir != null ? actualSortDir : "ASC";
            List<SortedContentResponseDto.SortInfo> sortInfoList = List.of(
                new SortedContentResponseDto.SortInfo(sortProperty, sortDirection)
            );
            SortedContentResponseDto<ProductDto> errorResponse = SortedContentResponseDto.of(List.of(), sortInfoList);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/api/v1/shopping-store/with-content")
    public ResponseEntity<SortedContentResponseDto<ProductDto>> getProductsByCategoryWithContent(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort) {
        try {
            int maxSize = validationService.validatePageSize(size);
            
            String actualSortBy = validationService.validateSortField(sortBy);
            String actualSortDir = validationService.validateSortDirection(sortDir);

            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length >= 1) {
                    actualSortBy = sortParts[0];
                }
                if (sortParts.length >= 2) {
                    actualSortDir = sortParts[1].toUpperCase();
                }
            }

            actualSortBy = validationService.validateSortField(actualSortBy);
            actualSortDir = validationService.validateSortDirection(actualSortDir);
            
            List<ProductDto> products = productService.getProductsByCategory(category, page, maxSize, actualSortBy, actualSortDir);
            String sortProperty = actualSortBy != null ? actualSortBy : "productName";
            String sortDirection = actualSortDir != null ? actualSortDir : "ASC";
            List<SortedContentResponseDto.SortInfo> sortInfoList = List.of(
                new SortedContentResponseDto.SortInfo(sortProperty, sortDirection)
            );
            SortedContentResponseDto<ProductDto> response = SortedContentResponseDto.of(products, sortInfoList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting products by category with content: {}", e.getMessage());
            String actualSortBy = validationService.validateSortField(sortBy);
            String actualSortDir = validationService.validateSortDirection(sortDir);

            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length >= 1) {
                    actualSortBy = sortParts[0];
                }
                if (sortParts.length >= 2) {
                    actualSortDir = sortParts[1].toUpperCase();
                }
            }

            actualSortBy = validationService.validateSortField(actualSortBy);
            actualSortDir = validationService.validateSortDirection(actualSortDir);
            
            String sortProperty = actualSortBy != null ? actualSortBy : "productName";
            String sortDirection = actualSortDir != null ? actualSortDir : "ASC";
            List<SortedContentResponseDto.SortInfo> sortInfoList = List.of(
                new SortedContentResponseDto.SortInfo(sortProperty, sortDirection)
            );
            SortedContentResponseDto<ProductDto> errorResponse = SortedContentResponseDto.of(List.of(), sortInfoList);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping(value = "/api/v1/shopping-store/paginated", params = {"!category"})
    public ResponseEntity<PagedResponseDto<ProductDto>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort) {
        try {
            int maxSize = validationService.validatePageSize(size);
            return getProductsPaginated(null, page, maxSize, sortBy, sortDir, sort);
        } catch (Exception e) {
            log.error("Error getting all paginated products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/api/v1/shopping-store/paginated")
    public ResponseEntity<PagedResponseDto<ProductDto>> getProductsPaginated(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) String sort) {
        try {
            int maxSize = validationService.validatePageSize(size);
            
            String actualSortBy = validationService.validateSortField(sortBy);
            String actualSortDir = validationService.validateSortDirection(sortDir);

            if (sort != null && !sort.isEmpty()) {
                String[] sortParts = sort.split(",");
                if (sortParts.length >= 1) {
                    actualSortBy = sortParts[0];
                }
                if (sortParts.length >= 2) {
                    actualSortDir = sortParts[1].toUpperCase();
                }
            }

            actualSortBy = validationService.validateSortField(actualSortBy);
            actualSortDir = validationService.validateSortDirection(actualSortDir);
            
            Page<ProductEntity> entityPage = productService.getProductsPage(category, page, maxSize, actualSortBy, actualSortDir);
            List<ProductDto> content = entityPage.getContent().stream()
                    .map(productMapper::toDto)
                    .collect(Collectors.toList());
            
            PagedResponseDto<ProductDto> response = new PagedResponseDto<>(
                    content,
                    entityPage.getTotalElements(),
                    entityPage.getTotalPages(),
                    page,
                    maxSize,
                    entityPage.isFirst(),
                    entityPage.isLast()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting paginated products: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> createProduct(@Valid ProductDto productDto) {
        try {
            ProductDto createdProduct = productService.createProduct(productDto);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> updateProduct(@Valid ProductDto productDto) {
        try {
            ProductDto updatedProduct = productService.updateProduct(productDto);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> removeProductFromStore(@NotNull UUID productId) {
        try {
            ProductDto updatedProduct = productService.removeProductFromStoreWithResult(productId);
            return ResponseEntity.ok(updatedProduct);
        } catch (ProductNotFoundException e) {
            log.error("Product not found for removal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error removing product from store: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> setProductQuantityState(@NotNull String productId, @NotNull String quantityState) {
        try {
            UUID productIdUuid = validationService.validateAndConvertProductId(productId);
            ProductDto result = productService.setProductQuantityState(productIdUuid, quantityState);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error setting product quantity state: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<ProductDto> getProductById(@NotNull UUID productId) {
        try {
            ProductDto product = productService.getProductById(productId);
            return ResponseEntity.ok(product);
        } catch (ProductNotFoundException e) {
            log.error("Product not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Error getting product by id: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}