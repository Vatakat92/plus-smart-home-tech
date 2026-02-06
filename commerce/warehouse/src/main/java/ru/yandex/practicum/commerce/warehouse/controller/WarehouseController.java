package ru.yandex.practicum.commerce.warehouse.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import ru.yandex.practicum.commerce.dto.*;
import ru.yandex.practicum.commerce.contract.warehouse.WarehouseOperations;
import ru.yandex.practicum.commerce.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService service;

    @Override
    public void addNewProductToWarehouse(@Valid NewProductInWarehouseRequest request) {
        log.info("Adding new product: {}", request);
        service.addNewProductToWarehouse(request);
    }

    @Override
    public BookedProductsDto checkAvailability(@Valid ShoppingCartDto cart) {
        log.info("Checking availability: {}", cart);
        return service.checkAvailability(cart);
    }

    @Override
    public void addProductQuantity(@Valid AddProductToWarehouseRequest request) {
        log.info("Adding quantity: {}", request);
        service.addProductQuantity(request);
    }

    @Override
    public AddressDto getAddress() {
        log.info("Getting warehouse address");
        return service.getAddress();
    }
}
