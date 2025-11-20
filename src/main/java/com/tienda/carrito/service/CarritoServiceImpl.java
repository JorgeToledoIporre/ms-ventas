package com.tienda.carrito.service;

import com.tienda.carrito.dto.*;
import com.tienda.carrito.entity.Carrito;
import com.tienda.carrito.entity.CarritoItem;
import com.tienda.carrito.exception.CarritoNotFoundException;
import com.tienda.carrito.exception.ProductoNotFoundException;
import com.tienda.carrito.mapper.CarritoMapper;
import com.tienda.carrito.repository.CarritoRepository;
import com.tienda.carrito.repository.CarritoItemRepository;
import com.tienda.carrito.service.CarritoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private CarritoMapper mapper;

    @Override
    public CarritoDTO obtenerCarritoPorUsuario(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return mapper.toDTO(carrito);
    }

    @Override
    public CarritoItemDTO agregarProducto(Long usuarioId, AgregarProductoRequest request) {
        // 1. Obtener o crear carrito
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        
        // 2. Verificar si el producto ya existe en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
            .findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId());
        
        CarritoItem item;
        
        if (itemExistente.isPresent()) {
            // 3a. Si existe, sumar cantidades
            item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            
            if (nuevaCantidad > 99) {
                throw new IllegalArgumentException("La cantidad máxima por producto es 99");
            }
            
            item.setCantidad(nuevaCantidad);
        } else {
            // 3b. Si no existe, crear nuevo item
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProductoId(request.getProductoId());
            item.setNombre(request.getNombre());
            item.setDescripcion(request.getDescripcion());
            item.setPrecio(request.getPrecio());
            item.setImagen(request.getImagen());
            item.setCantidad(request.getCantidad());
            
            carrito.agregarItem(item);
        }
        
        // 4. Guardar
        CarritoItem guardado = carritoItemRepository.save(item);
        
        return mapper.toItemDTO(guardado);
    }

    @Override
    public CarritoItemDTO actualizarCantidad(Long usuarioId, Long itemId, Integer nuevaCantidad) {
        // 1. Obtener el item
        CarritoItem item = carritoItemRepository.findById(itemId)
            .orElseThrow(() -> new ProductoNotFoundException("Item no encontrado"));
        
        // 2. Verificar que pertenece al usuario
        if (!item.getCarrito().getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Este item no pertenece a tu carrito");
        }
        
        // 3. Actualizar cantidad
        item.setCantidad(nuevaCantidad);
        CarritoItem actualizado = carritoItemRepository.save(item);
        
        return mapper.toItemDTO(actualizado);
    }

    @Override
    public void eliminarItem(Long usuarioId, Long itemId) {
        // 1. Obtener el item
        CarritoItem item = carritoItemRepository.findById(itemId)
            .orElseThrow(() -> new ProductoNotFoundException("Item no encontrado"));
        
        // 2. Verificar que pertenece al usuario
        if (!item.getCarrito().getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException("Este item no pertenece a tu carrito");
        }
        
        // 3. Eliminar
        carritoItemRepository.delete(item);
    }

    @Override
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioIdAndActivoTrue(usuarioId)
            .orElseThrow(() -> new CarritoNotFoundException("Carrito no encontrado"));
        
        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    @Override
    public PedidoDTO procesarCheckout(Long usuarioId, CheckoutRequest request) {
        // 1. Obtener carrito
        Carrito carrito = carritoRepository.findByUsuarioIdAndActivoTrue(usuarioId)
            .orElseThrow(() -> new CarritoNotFoundException("Carrito vacío"));
        
        if (carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }
        
        // 2. Calcular totales
        Double subtotal = carrito.calcularSubtotal();
        Double descuento = 0.0;
        
        // Aplicar descuento si existe
        if ("FELICES50".equalsIgnoreCase(request.getCodigoDescuento())) {
            descuento = subtotal * 0.10;
        }
        
        Double total = subtotal - descuento;
        
        // 3. Crear pedido (simulado)
        PedidoDTO pedido = new PedidoDTO();
        pedido.setId(System.currentTimeMillis());
        pedido.setFecha(LocalDateTime.now());
        pedido.setCliente(request.getCliente());
        pedido.setProductos(carrito.getItems().stream()
            .map(mapper::toItemDTO)
            .toList());
        pedido.setSubtotal(subtotal);
        pedido.setDescuento(descuento);
        pedido.setTotal(total);
        
        // 4. Desactivar carrito
        carrito.setActivo(false);
        carritoRepository.save(carrito);
        
        return pedido;
    }
    
    // Método auxiliar
    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioIdAndActivoTrue(usuarioId)
            .orElseGet(() -> {
                Carrito nuevoCarrito = new Carrito();
                nuevoCarrito.setUsuarioId(usuarioId);
                nuevoCarrito.setActivo(true);
                return carritoRepository.save(nuevoCarrito);
            });
    }
}