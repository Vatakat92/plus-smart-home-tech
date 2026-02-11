package ru.yandex.practicum.commerce.delivery.exception;

public class InvalidDeliveryException extends RuntimeException {
    public InvalidDeliveryException(String message) {
        super(message);
    }

    public InvalidDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
