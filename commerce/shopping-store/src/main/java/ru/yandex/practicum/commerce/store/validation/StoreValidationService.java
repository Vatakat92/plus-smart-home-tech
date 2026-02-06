package ru.yandex.practicum.commerce.store.validation;

import java.util.UUID;

public interface StoreValidationService {
    
    UUID validateAndConvertProductId(String productId);
    
    String validateSortField(String sortBy);
    
    String validateSortDirection(String sortDir);
    
    int validatePageSize(int size);
}