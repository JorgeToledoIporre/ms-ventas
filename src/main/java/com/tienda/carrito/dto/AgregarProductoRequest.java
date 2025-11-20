package com.tienda.carrito.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AgregarProductoRequest {
    
    @NotNull(message = "El ID del producto es obligatorio")
    @Positive(message = "El ID del producto debe ser positivo")
    private Long productoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 99, message = "La cantidad máxima es 99")
    private Integer cantidad;
    
    // Datos del producto (vienen del frontend)
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;
    
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser positivo")
    private Double precio;
    
    private String imagen;
}