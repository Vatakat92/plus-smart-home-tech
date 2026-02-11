package ru.yandex.practicum.commerce.payment.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.ErrorResponseDto;
import ru.yandex.practicum.commerce.payment.exception.InvalidPaymentException;
import ru.yandex.practicum.commerce.payment.exception.PaymentFailedException;
import ru.yandex.practicum.commerce.payment.exception.PaymentNotFoundException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Input validation failed")
                .details(ex.getBindingResult().getFieldErrors().stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toArray(String[]::new))
                .build();

        log.error("Validation error: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentNotFoundException(PaymentNotFoundException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Payment Not Found")
                .message(ex.getMessage())
                .build();

        log.error("Payment not found: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponseDto> handlePaymentFailedException(PaymentFailedException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Payment Failed")
                .message(ex.getMessage())
                .build();

        log.error("Payment failed: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidPaymentException(InvalidPaymentException ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Payment")
                .message(ex.getMessage())
                .build();

        log.error("Invalid payment: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto errorResponse = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .build();

        log.error("Unexpected error: ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
