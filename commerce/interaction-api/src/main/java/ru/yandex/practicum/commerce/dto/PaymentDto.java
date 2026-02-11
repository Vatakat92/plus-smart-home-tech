package ru.yandex.practicum.commerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID paymentId;

    @NotNull(message = "Total payment cannot be null")
    @Positive(message = "Total payment must be positive")
    private BigDecimal totalPayment;

    @NotNull(message = "Delivery total cannot be null")
    @Positive(message = "Delivery total must be positive")
    private BigDecimal deliveryTotal;

    @NotNull(message = "Fee total cannot be null")
    @Positive(message = "Fee total must be positive")
    private BigDecimal feeTotal;
}
