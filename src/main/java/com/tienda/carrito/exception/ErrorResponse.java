package com.tienda.carrito.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String mensaje;
    private Map<String, String> errores;
    private LocalDateTime timestamp;
}