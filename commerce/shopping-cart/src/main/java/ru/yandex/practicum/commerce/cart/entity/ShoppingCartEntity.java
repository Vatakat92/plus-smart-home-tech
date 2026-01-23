package ru.yandex.practicum.commerce.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@ToString
public class ShoppingCartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cart_id", updatable = false, nullable = false)
    private UUID cartId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "products", columnDefinition = "jsonb")
    @ToString.Exclude
    private Map<UUID, Integer> products = new HashMap<>();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> oEffectiveClass = o instanceof HibernateProxy ?
                ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() :
                o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ?
                ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() :
                this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;

        ShoppingCartEntity that = (ShoppingCartEntity) o;
        return getCartId() != null && Objects.equals(getCartId(), that.getCartId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}