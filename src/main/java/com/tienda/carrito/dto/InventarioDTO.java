package com.tienda.carrito.dto;

import lombok.Data;

@Data
public class InventarioDTO {
    private Long id;
    private Integer stock;
    private Integer minimoStock;
}