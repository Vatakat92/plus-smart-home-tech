package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.commerce.warehouse.entity.ProductOnWarehouseEntity;

import java.util.Optional;
import java.util.UUID;

public interface ProductOnWarehouseRepository extends JpaRepository<ProductOnWarehouseEntity, UUID> {
    Optional<ProductOnWarehouseEntity> findByProductId(UUID productId);
}