package ru.yandex.practicum.commerce.warehouse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBooking {

    @Id
    @UuidGenerator
    private UUID bookingId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @ElementCollection
    @CollectionTable(name = "order_booking_products", joinColumns = @JoinColumn(name = "booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products;

    @Column(name = "delivery_id")
    private UUID deliveryId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
