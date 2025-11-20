package com.tienda.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {
    private Long id;
    private Long productoId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String imagen;
    private Integer cantidad;
    private Double subtotal;
}