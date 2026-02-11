package ru.yandex.practicum.commerce.delivery.exception;

public class DeliveryStateException extends RuntimeException {
    public DeliveryStateException(String message) {
        super(message);
    }

    public DeliveryStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
