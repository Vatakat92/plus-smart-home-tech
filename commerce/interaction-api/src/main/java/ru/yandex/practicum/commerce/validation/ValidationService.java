package ru.yandex.practicum.commerce.validation;

import java.util.UUID;

public interface ValidationService {
    
    UUID validateAndConvertProductId(String productId);
    
    String validateSortField(String sortBy);
    
    String validateSortDirection(String sortDir);
    
    int validatePageSize(int size);
}