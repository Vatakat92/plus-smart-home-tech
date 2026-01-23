package ru.yandex.practicum.commerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {
    
    private LocalDateTime timestamp;
    
    private int status;
    
    private String error;
    
    private String message;
    
    private Object details;
}