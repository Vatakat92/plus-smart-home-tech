package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    private UUID shoppingCartId;
    
    @NotNull(message = "Products map cannot be null")
    @Valid
    private Map<UUID, Integer> products;
}