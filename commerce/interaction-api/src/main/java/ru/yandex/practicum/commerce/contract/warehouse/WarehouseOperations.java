package ru.yandex.practicum.commerce.contract.warehouse;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import ru.yandex.practicum.commerce.dto.*;

public interface WarehouseOperations {

    @PutMapping
    void addNewProductToWarehouse(@Valid @RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkAvailability(@Valid @RequestBody ShoppingCartDto cart);

    @PostMapping("/add")
    void addProductQuantity(@Valid @RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getAddress();
}