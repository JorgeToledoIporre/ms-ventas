package com.tienda.carrito.mapper;

import com.tienda.carrito.client.ProductoClient;
import com.tienda.carrito.dto.CarritoDTO;
import com.tienda.carrito.dto.CarritoItemDTO;
import com.tienda.carrito.dto.ProductoDTO;
import com.tienda.carrito.entity.Carrito;
import com.tienda.carrito.entity.CarritoItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CarritoMapper {
    
    @Autowired
    private ProductoClient productoClient;
    
    public CarritoDTO toDTO(Carrito carrito) {
        if (carrito == null) {
            return null;
        }
        
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setUsuarioId(carrito.getUsuarioId());
        
        // Enriquecer cada item con datos del producto
        dto.setItems(carrito.getItems().stream()
            .map(this::toItemDTOEnriquecido)
            .collect(Collectors.toList()));
            
        dto.setSubtotal(carrito.calcularSubtotal());
        dto.setTotalItems(carrito.contarTotalItems());
        dto.setFechaCreacion(carrito.getFechaCreacion());
        dto.setFechaActualizacion(carrito.getFechaActualizacion());
        
        return dto;
    }
    
    /**
     * Convierte CarritoItem a DTO y enriquece con datos del producto
     */
    public CarritoItemDTO toItemDTOEnriquecido(CarritoItem item) {
        try {
            // Obtener datos actuales del producto
            ProductoDTO producto = productoClient.obtenerProducto(item.getProductoId());
            return toItemDTOEnriquecido(item, producto);
        } catch (Exception e) {
            // Si falla, retornar con datos mínimos
            CarritoItemDTO dto = new CarritoItemDTO();
            dto.setId(item.getId());
            dto.setProductoId(item.getProductoId());
            dto.setCantidad(item.getCantidad());
            dto.setNombre("Producto no disponible");
            dto.setPrecio(item.getPrecioAlAgregar() != null ? item.getPrecioAlAgregar() : 0.0);
            dto.setSubtotal(dto.getPrecio() * item.getCantidad());
            return dto;
        }
    }
    
    /**
     * Convierte CarritoItem + ProductoDTO a CarritoItemDTO
     */
    public CarritoItemDTO toItemDTOEnriquecido(CarritoItem item, ProductoDTO producto) {
        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setId(item.getId());
        dto.setProductoId(item.getProductoId());
        dto.setCantidad(item.getCantidad());
        
        // Datos del producto (obtenidos del MS-Inventario)
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setImagen(producto.getImagen());
        
        // Precio: usar el congelado o el actual
        Double precio = item.getPrecioAlAgregar() != null 
            ? item.getPrecioAlAgregar() 
            : (producto.getPrecioOferta() != null ? producto.getPrecioOferta() : producto.getPrecio());
        
        dto.setPrecio(precio);
        dto.setSubtotal(precio * item.getCantidad());
        
        return dto;
    }
}