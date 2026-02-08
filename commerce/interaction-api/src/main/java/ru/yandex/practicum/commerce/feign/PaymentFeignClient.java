package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.payment.PaymentOperations;

@FeignClient(name = "payment")
public interface PaymentFeignClient extends PaymentOperations {
}
