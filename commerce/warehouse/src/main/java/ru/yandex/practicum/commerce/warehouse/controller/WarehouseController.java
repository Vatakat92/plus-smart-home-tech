package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
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
    public void addNewProductToWarehouse(@Valid NewProductInWarehouseRequest request) {
        warehouseService.addNewProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkAvailability(@Valid ShoppingCartDto cart) {
        return warehouseService.checkAvailability(cart);
    }

    @Override
    public void addProductQuantity(@Valid AddProductToWarehouseRequest request) {
        warehouseService.addProductQuantity(request);
    }

    @Override
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }
}