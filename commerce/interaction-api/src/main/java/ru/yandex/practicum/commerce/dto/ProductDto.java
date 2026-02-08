package ru.yandex.practicum.commerce.dto;

import lombok.Data;
import ru.yandex.practicum.commerce.enums.ProductState;
import ru.yandex.practicum.commerce.enums.QuantityState;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDto {
    private UUID productId;
    
    @NotBlank(message = "Product name cannot be blank")
    private String productName;
    
    private String description;
    private String imageSrc;
    
    @NotNull(message = "Quantity state cannot be null")
    private QuantityState quantityState;

    private ProductState productState;

    @NotNull(message = "Product category cannot be null")
    private ProductCategory productCategory;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
}