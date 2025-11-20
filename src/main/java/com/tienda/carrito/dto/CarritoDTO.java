package com.tienda.carrito.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoDTO {
    private Long id;
    private Long usuarioId;
    private List<CarritoItemDTO> items;
    private Double subtotal;
    private Integer totalItems;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}