package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseFeignClient {
    
    @PutMapping("/api/v1/warehouse")
    void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    BookedProductsDto checkAvailability(@RequestBody ShoppingCartDto cart);

    @PostMapping("/api/v1/warehouse/add")
    void addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    AddressDto getAddress();
}