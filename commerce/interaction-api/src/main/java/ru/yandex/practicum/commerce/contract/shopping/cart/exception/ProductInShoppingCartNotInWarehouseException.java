package ru.yandex.practicum.commerce.contract.shopping.cart.exception;

public class ProductInShoppingCartNotInWarehouseException extends Exception {
    public ProductInShoppingCartNotInWarehouseException(String message) {
        super(message);
    }
    
    public ProductInShoppingCartNotInWarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}