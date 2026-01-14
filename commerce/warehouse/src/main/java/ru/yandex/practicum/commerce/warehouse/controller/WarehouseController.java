package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.WarehouseFeignClient;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class WarehouseController implements WarehouseFeignClient {
    
    private final WarehouseService warehouseService;
    
    @Override
    public ResponseEntity<Void> addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        try {
            warehouseService.addNewProductToWarehouse(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error adding new product to warehouse: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<BookedProductsDto> checkAvailability(ShoppingCartDto cart) {
        try {
            BookedProductsDto result = warehouseService.checkAvailability(cart);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> addProductQuantity(AddProductToWarehouseRequest request) {
        try {
            warehouseService.addProductQuantity(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error adding product quantity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<AddressDto> getAddress() {
        try {
            AddressDto address = warehouseService.getAddress();
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            log.error("Error getting warehouse address: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}