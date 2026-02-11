package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Data
public class ChangeProductQuantityRequest {
    @NotNull(message = "Product ID cannot be null")
    private UUID productId;
    
    @Positive(message = "New quantity must be positive")
    private Long newQuantity;
}