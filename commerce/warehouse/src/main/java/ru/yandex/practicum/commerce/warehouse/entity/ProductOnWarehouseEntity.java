package ru.yandex.practicum.commerce.warehouse.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.commerce.warehouse.util.RandomAddressGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products_on_warehouse")
@Getter
@Setter
@ToString
public class ProductOnWarehouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "fragile", nullable = false)
    private Boolean fragile = false;

    @Column(name = "width")
    private Double width;

    @Column(name = "height")
    private Double height;

    @Column(name = "depth")
    private Double depth;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "address", nullable = false)
    private String address = RandomAddressGenerator.getCurrentAddress();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}