package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.shopping.store.ShoppingStoreOperations;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreFeignClient extends ShoppingStoreOperations {
}
