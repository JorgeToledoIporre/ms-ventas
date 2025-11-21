package com.tienda.carrito.client;

import com.tienda.carrito.dto.InventarioDTO;
import com.tienda.carrito.dto.ProductoDTO;
import com.tienda.carrito.exception.ProductoNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

@Component
public class ProductoClient {

    private final RestTemplate restTemplate;
    
    @Value("${microservicio.inventario.url:http://localhost:8081}")
    private String inventarioUrl;

    public ProductoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene un producto del microservicio de inventario
     */
    public ProductoDTO obtenerProducto(Long productoId) {
        try {
            String url = inventarioUrl + "/v1/productos/" + productoId;
            return restTemplate.getForObject(url, ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductoNotFoundException("Producto no encontrado: " + productoId);
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el microservicio de inventario", e);
        }
    }

    /**
     * Verifica si hay stock disponible
     */
    public boolean verificarStock(Long productoId, Integer cantidadSolicitada) {
        try {
            ProductoDTO producto = obtenerProducto(productoId);
            
            // Asumiendo que ProductoDTO tiene inventarioId
            if (producto.getInventarioId() == null) {
                return false;
            }
            
            // Llama al endpoint de inventario
            String url = inventarioUrl + "/v1/inventario/" + producto.getInventarioId();
            InventarioDTO inventario = restTemplate.getForObject(url, InventarioDTO.class);
            
            return inventario != null && inventario.getStock() >= cantidadSolicitada;
        } catch (Exception e) {
            return false; // Si falla, asumimos que no hay stock
        }
    }
}