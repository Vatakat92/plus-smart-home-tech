package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.order.OrderOperations;

@FeignClient(name = "order")
public interface OrderFeignClient extends OrderOperations {
}
