package ru.yandex.practicum.commerce.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.commerce.dto.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @UuidGenerator
    private UUID paymentId;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentState;
}
