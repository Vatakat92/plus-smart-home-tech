package ru.yandex.practicum.commerce.order.exception;

public class OrderStateException extends RuntimeException {
    public OrderStateException(String message) {
        super(message);
    }

    public OrderStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
