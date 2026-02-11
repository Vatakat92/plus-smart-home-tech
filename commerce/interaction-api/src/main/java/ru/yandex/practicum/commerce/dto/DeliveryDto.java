package ru.yandex.practicum.commerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    private UUID deliveryId;

    @NotNull(message = "From address cannot be null")
    @Valid
    private AddressDto fromAddress;

    @NotNull(message = "To address cannot be null")
    @Valid
    private AddressDto toAddress;

    @NotNull(message = "Order ID cannot be null")
    private UUID orderId;

    private DeliveryState deliveryState;
}
