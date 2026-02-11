package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;

@FeignClient(name = "warehouse")
public interface WarehouseFeignClient extends WarehouseOperations {
}