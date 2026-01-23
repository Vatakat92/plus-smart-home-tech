package ru.yandex.practicum.commerce.dto;

import lombok.Data;

import jakarta.validation.constraints.Positive;

@Data
public class DimensionDto {
    @Positive(message = "Width must be positive")
    private Double width;
    
    @Positive(message = "Height must be positive")
    private Double height;
    
    @Positive(message = "Depth must be positive")
    private Double depth;
}