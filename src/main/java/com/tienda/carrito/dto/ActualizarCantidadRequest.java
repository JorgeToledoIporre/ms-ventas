package com.tienda.carrito.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class ActualizarCantidadRequest {
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser 0 o mayor")
    private Integer cantidad; 
}