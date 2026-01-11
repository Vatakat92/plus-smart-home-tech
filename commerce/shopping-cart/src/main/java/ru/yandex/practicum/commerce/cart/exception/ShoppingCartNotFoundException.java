package ru.yandex.practicum.commerce.cart.exception;

public class ShoppingCartNotFoundException extends RuntimeException {
    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}