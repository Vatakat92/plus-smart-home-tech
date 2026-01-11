package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.feign.WarehouseFeignClient;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@RestController
@RequiredArgsConstructor
@Validated
public class WarehouseController implements WarehouseFeignClient {
    
    private final WarehouseService warehouseService;
    
    @Override
    public ResponseEntity<Void> addNewProductToWarehouse(@Valid NewProductInWarehouseRequest request) {
        try {
            warehouseService.addNewProductToWarehouse(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<BookedProductsDto> checkAvailability(@Valid ShoppingCartDto cart) {
        try {
            BookedProductsDto result = warehouseService.checkAvailability(cart);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<Void> addProductQuantity(@Valid AddProductToWarehouseRequest request) {
        try {
            warehouseService.addProductQuantity(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<AddressDto> getAddress() {
        try {
            AddressDto address = warehouseService.getAddress();
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}