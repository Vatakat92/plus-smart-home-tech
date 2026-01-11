package ru.yandex.practicum.commerce.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.commerce.cart.entity.ShoppingCartEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, UUID> {
    Optional<ShoppingCartEntity> findByUsername(String username);
}