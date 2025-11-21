package com.tienda.carrito.dto;

import lombok.Data;

@Data
public class ProductoDTO {
    private Long id;
    private String sku;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Double precioOferta;
    private String imagen;
    private Boolean destacado;
    private Long categoriaId;
    private Long inventarioId;
}