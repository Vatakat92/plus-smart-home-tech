package ru.yandex.practicum.commerce.contract.shopping.cart.exception;

public class NoProductsInShoppingCartException extends Exception {
    public NoProductsInShoppingCartException(String message) {
        super(message);
    }
    
    public NoProductsInShoppingCartException(String message, Throwable cause) {
        super(message, cause);
    }
}