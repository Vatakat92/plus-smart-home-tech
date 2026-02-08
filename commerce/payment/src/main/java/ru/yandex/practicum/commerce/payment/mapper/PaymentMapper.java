package ru.yandex.practicum.commerce.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.commerce.dto.PaymentDto;
import ru.yandex.practicum.commerce.payment.entity.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentId", source = "paymentId")
    @Mapping(target = "totalPayment", source = "totalPayment")
    @Mapping(target = "deliveryTotal", source = "deliveryTotal")
    @Mapping(target = "feeTotal", source = "feeTotal")
    PaymentDto toDto(Payment payment);
}
