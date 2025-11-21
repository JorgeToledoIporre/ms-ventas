package com.tienda.carrito.service;

import com.tienda.carrito.client.ProductoClient;
import com.tienda.carrito.dto.*;
import com.tienda.carrito.entity.Carrito;
import com.tienda.carrito.entity.CarritoItem;
import com.tienda.carrito.exception.*;
import com.tienda.carrito.mapper.CarritoMapper;
import com.tienda.carrito.repository.CarritoRepository;
import com.tienda.carrito.repository.CarritoItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    
    @Autowired
    private CarritoItemRepository carritoItemRepository;
    
    @Autowired
    private CarritoMapper mapper;
    
    @Autowired
    private ProductoClient productoClient;

    // --- 1. AGREGAR PRODUCTO ---
    @Override
    public CarritoItemDTO agregarProducto(Long usuarioId, AgregarProductoRequest request) {
        ProductoDTO producto = productoClient.obtenerProducto(request.getProductoId());
        if (producto == null) {
            throw new ProductoNotFoundException("Producto no encontrado");
        }
        
        if (!productoClient.verificarStock(request.getProductoId(), request.getCantidad())) {
            throw new StockInsuficienteException("Stock insuficiente");
        }
        
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        
        Optional<CarritoItem> itemExistente = carritoItemRepository
            .findByCarritoIdAndProductoId(carrito.getId(), request.getProductoId());
        
        CarritoItem item;
        
        if (itemExistente.isPresent()) {
            item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + request.getCantidad();
            
            if (!productoClient.verificarStock(request.getProductoId(), nuevaCantidad)) {
                throw new StockInsuficienteException("Stock insuficiente para la nueva cantidad");
            }
            item.setCantidad(nuevaCantidad);
        } else {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProductoId(request.getProductoId());
            item.setCantidad(request.getCantidad());
            
            // Snapshot del precio
            Double precioFinal = (producto.getPrecioOferta() != null) ? 
                                 producto.getPrecioOferta() : producto.getPrecio();
            item.setPrecioAlAgregar(precioFinal);
            
            carrito.agregarItem(item);
        }
        
        CarritoItem guardado = carritoItemRepository.save(item);
        return mapper.toItemDTOEnriquecido(guardado, producto);
    }

    // --- 2. OBTENER CARRITO ---
    @Override
    public CarritoDTO obtenerCarritoPorUsuario(Long usuarioId) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        return mapper.toDTO(carrito);
    }

    // --- 3. ACTUALIZAR CANTIDAD ---
    @Override
    public CarritoItemDTO actualizarCantidad(Long usuarioId, Long itemId, Integer cantidad) {
        if (cantidad <= 0) {
            eliminarItem(usuarioId, itemId);
            return null;
        }

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item no encontrado"));
        
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("No autorizado");
        }

        if (!productoClient.verificarStock(item.getProductoId(), cantidad)) {
            throw new StockInsuficienteException("No hay suficiente stock");
        }

        item.setCantidad(cantidad);
        CarritoItem itemGuardado = carritoItemRepository.save(item);
        
        // Obtenemos producto fresco para el DTO
        ProductoDTO prodInfo = productoClient.obtenerProducto(item.getProductoId());
        return mapper.toItemDTOEnriquecido(itemGuardado, prodInfo);
    }

    // --- 4. ELIMINAR ITEM ---
    @Override
    public void eliminarItem(Long usuarioId, Long itemId) {
        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item no encontrado"));
                
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        if (!item.getCarrito().getId().equals(carrito.getId())) {
            throw new RuntimeException("No autorizado");
        }

        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);
    }

    // --- 5. VACIAR CARRITO ---
    @Override
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new CarritoNotFoundException("Carrito no encontrado"));
        
        carritoItemRepository.deleteAll(carrito.getItems());
        carrito.getItems().clear();
        // Guardamos para asegurar consistencia si usas Cascade
        carritoRepository.save(carrito);
    }

    // --- 6. PROCESAR CHECKOUT (Ajustado a PedidoDTO) ---
    @Override
    public PedidoDTO procesarCheckout(Long usuarioId, CheckoutRequest request) {
        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        
        if (carrito.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // 1. Validar stock final
        for (CarritoItem item : carrito.getItems()) {
            if (!productoClient.verificarStock(item.getProductoId(), item.getCantidad())) {
                throw new StockInsuficienteException("Sin stock para el producto ID: " + item.getProductoId());
            }
        }

        // 2. Calcular totales
        Double total = carrito.calcularSubtotal();
        
        // 3. Crear el PedidoDTO de respuesta (Simulando la creación de la orden)
        PedidoDTO pedidoResponse = new PedidoDTO();
        pedidoResponse.setId(System.currentTimeMillis()); // ID simulado
        pedidoResponse.setFecha(LocalDateTime.now());
        pedidoResponse.setCliente(request.getCliente());
        pedidoResponse.setSubtotal(total);
        pedidoResponse.setDescuento(0.0);
        pedidoResponse.setTotal(total);
        
        // Convertimos los items del carrito a items del pedido
        // (Asumiendo que quieres devolver la lista de lo comprado)
        List<CarritoItemDTO> productosComprados = carrito.getItems().stream()
            .map(item -> mapper.toItemDTOEnriquecido(item))
            .collect(Collectors.toList());
        pedidoResponse.setProductos(productosComprados);

        // 4. Vaciar carrito
        vaciarCarrito(usuarioId);

        return pedidoResponse;
    }

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setUsuarioId(usuarioId);
                    return carritoRepository.save(c);
                });
    }
}