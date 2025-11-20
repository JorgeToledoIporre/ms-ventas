package com.tienda.carrito.mapper;

import com.tienda.carrito.dto.CarritoDTO;
import com.tienda.carrito.dto.CarritoItemDTO;
import com.tienda.carrito.entity.Carrito;
import com.tienda.carrito.entity.CarritoItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CarritoMapper {
    
    public CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) {
            return null;
        }
        
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setUsuarioId(carrito.getUsuarioId());
        dto.setItems(carrito.getItems().stream()
            .map(this::toItemDTO)
            .collect(Collectors.toList()));
        dto.setSubtotal(carrito.calcularSubtotal());
        dto.setTotalItems(carrito.contarTotalItems());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setFechaActualizacion(carrito.getFechaActualizacion());
        
        return dto;
    }
    
    public CarritoItemDTO toItemDTO(CarritoItem item) {
        if (item == null) {
            return null;
        }
        
        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setNombre(item.getNombre());
        dto.setDescripcion(item.getDescripcion());
        dto.setPrecio(item.getPrecio());
        dto.setImagen(item.getImagen());
        dto.setCantidad(item.getCantidad());
        dto.setSubtotal(item.calcularSubtotal());
        
        return dto;
    }
}