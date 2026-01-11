package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Data
public class SetProductQuantityStateRequest {
    @NotNull(message = "Product ID cannot be null")
    private UUID productId;
    
    @NotBlank(message = "Quantity state cannot be blank")
    private String quantityState;
}