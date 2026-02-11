package ru.yandex.practicum.commerce.contract.shopping.cart.exception;

public class ProductInShoppingCartLowQuantityInWarehouseException extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouseException(String message) {
        super(message);
    }
    
    public ProductInShoppingCartLowQuantityInWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}