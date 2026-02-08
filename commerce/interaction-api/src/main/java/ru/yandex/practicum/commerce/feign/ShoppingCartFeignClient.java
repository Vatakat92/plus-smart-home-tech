package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.shopping.cart.ShoppingCartOperations;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartFeignClient extends ShoppingCartOperations {
}
