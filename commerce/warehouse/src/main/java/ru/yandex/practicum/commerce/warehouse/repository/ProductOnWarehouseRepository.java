package ru.yandex.practicum.commerce.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.warehouse.entity.ProductOnWarehouseEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductOnWarehouseRepository extends JpaRepository<ProductOnWarehouseEntity, UUID> {
    Optional<ProductOnWarehouseEntity> findByProductId(UUID productId);
}