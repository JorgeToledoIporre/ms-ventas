package com.tienda.carrito.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private LocalDateTime fecha;
    private ClienteDTO cliente;
    private List<CarritoItemDTO> productos;
    private Double subtotal;
    private Double descuento;
    private Double total;
}