package com.tienda.carrito.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ActualizarCantidadRequest {
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 99, message = "La cantidad máxima es 99")
    private Integer cantidad;
}