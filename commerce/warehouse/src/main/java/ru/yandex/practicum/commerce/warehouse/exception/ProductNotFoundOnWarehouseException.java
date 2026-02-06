package ru.yandex.practicum.commerce.warehouse.exception;

public class ProductNotFoundOnWarehouseException extends RuntimeException {
    public ProductNotFoundOnWarehouseException(String message) {
        super(message);
    }
}
