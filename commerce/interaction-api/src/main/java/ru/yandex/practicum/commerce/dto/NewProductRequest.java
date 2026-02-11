package ru.yandex.practicum.commerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewProductRequest {
    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc;

    @NotNull
    private ProductCategory productCategory;

    @NotNull
    @Positive
    private BigDecimal price;
}
