package ru.yandex.practicum.commerce.store.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.commerce.exception.ResourceNotFoundException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class StoreValidationServiceImpl implements StoreValidationService {
    
    private static final Set<String> ALLOWED_SORT_FIELDS = new HashSet<>(Arrays.asList(
        "productName", "price", "productCategory", "quantityState", "productState", "createdAt"
    ));
    
    private static final Set<String> ALLOWED_SORT_DIRECTIONS = new HashSet<>(Arrays.asList("ASC", "DESC"));
    
    @Override
    public UUID validateAndConvertProductId(String productId) {
        try {
            return UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            log.error("Invalid product ID format: {}", productId);
            throw new ResourceNotFoundException("Invalid product ID format: " + productId);
        }
    }
    
    @Override
    public String validateSortField(String sortBy) {
        if (sortBy == null) {
            return "productName";
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            log.warn("Invalid sort field: {}, using default productName", sortBy);
            return "productName";
        }
        return sortBy;
    }
    
    @Override
    public String validateSortDirection(String sortDir) {
        if (sortDir == null) {
            return "ASC";
        }
        String upperSortDir = sortDir.toUpperCase();
        if (!ALLOWED_SORT_DIRECTIONS.contains(upperSortDir)) {
            log.warn("Invalid sort direction: {}, using default ASC", sortDir);
            return "ASC";
        }
        return upperSortDir;
    }
    
    @Override
    public int validatePageSize(int size) {
        int maxSize = Math.min(size, 100);
        if (size != maxSize) {
            log.warn("Requested size {} exceeds maximum allowed size, using {} instead", size, maxSize);
        }
        return maxSize;
    }
}