package com.tienda.carrito.service;

import com.tienda.carrito.dto.*;

public interface CarritoService {
    
    CarritoDTO obtenerCarritoPorUsuario(Long usuarioId);
    
    CarritoItemDTO agregarProducto(Long usuarioId, AgregarProductoRequest request);
    
    CarritoItemDTO actualizarCantidad(Long usuarioId, Long itemId, Integer nuevaCantidad);
    
    void eliminarItem(Long usuarioId, Long itemId);
    
    void vaciarCarrito(Long usuarioId);
    
    PedidoDTO procesarCheckout(Long usuarioId, CheckoutRequest request);
}