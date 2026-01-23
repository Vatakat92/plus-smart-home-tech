package ru.yandex.practicum.commerce.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.commerce.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseFeignClient {
    
    @PutMapping("/api/v1/warehouse")
    ResponseEntity<Void> addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/api/v1/warehouse/check")
    ResponseEntity<BookedProductsDto> checkAvailability(@RequestBody ShoppingCartDto cart);

    @PostMapping("/api/v1/warehouse/add")
    ResponseEntity<Void> addProductQuantity(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/api/v1/warehouse/address")
    ResponseEntity<AddressDto> getAddress();
}