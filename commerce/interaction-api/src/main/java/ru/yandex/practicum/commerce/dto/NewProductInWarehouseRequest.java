package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Data
public class NewProductInWarehouseRequest {
    @NotNull(message = "Product ID cannot be null")
    private UUID productId;
    
    private Boolean fragile;
    
    @Valid
    private DimensionDto dimension;
    
    @Positive(message = "Weight must be positive")
    private Double weight;
}