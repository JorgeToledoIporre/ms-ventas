package com.tienda.carrito.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor // Genera constructor con: status, message, errors, timestamp
@NoArgsConstructor
public class ErrorResponse {
    
    private int status;
    private String message;
    private Map<String, String> errors; // Usado para errores de validación
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this(status, message, null, timestamp); // Llama al constructor AllArgsConstructor con errors=null
    }
}