package com.tienda.carrito.controller;

import com.tienda.carrito.dto.*;
import com.tienda.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/carrito")
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
@CrossOrigin(origins = "*") // En producción, especifica el dominio del frontend
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    /**
     * Obtener carrito del usuario actual
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Devuelve el carrito activo del usuario")
    @ApiResponse(responseCode = "200", description = "Carrito encontrado")
    public ResponseEntity<CarritoDTO> obtenerCarrito() {
        Long usuarioId = obtenerUsuarioActual();
        CarritoDTO carrito = carritoService.obtenerCarritoPorUsuario(usuarioId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Agregar producto al carrito
     */
    @PostMapping("/items")
    @Operation(summary = "Agregar producto", description = "Agrega un producto al carrito o incrementa su cantidad")
    @ApiResponse(responseCode = "201", description = "Producto agregado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<CarritoItemDTO> agregarProducto(
            @Valid @RequestBody AgregarProductoRequest request) {
        
        Long usuarioId = obtenerUsuarioActual();
        CarritoItemDTO item = carritoService.agregarProducto(usuarioId, request);
        return ResponseEntity.status(201).body(item);
    }

    /**
     * Actualizar cantidad de un item
     */
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad", description = "Modifica la cantidad de un producto en el carrito")
    @ApiResponse(responseCode = "200", description = "Cantidad actualizada")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<CarritoItemDTO> actualizarCantidad(
            @PathVariable Long itemId,
            @Valid @RequestBody ActualizarCantidadRequest request) {
        
        Long usuarioId = obtenerUsuarioActual();
        CarritoItemDTO item = carritoService.actualizarCantidad(
            usuarioId, itemId, request.getCantidad()
        );
        return ResponseEntity.ok(item);
    }

    /**
     * Eliminar un item del carrito
     */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar item", description = "Elimina un producto del carrito")
    @ApiResponse(responseCode = "204", description = "Item eliminado")
    @ApiResponse(responseCode = "404", description = "Item no encontrado")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        Long usuarioId = obtenerUsuarioActual();
        carritoService.eliminarItem(usuarioId, itemId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vaciar carrito completo
     */
    @DeleteMapping
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    public ResponseEntity<Void> vaciarCarrito() {
        Long usuarioId = obtenerUsuarioActual();
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Procesar checkout (finalizar compra)
     */
    @PostMapping("/checkout")
    @Operation(summary = "Procesar pedido", description = "Finaliza la compra y crea el pedido")
    @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Carrito vacío o datos inválidos")
    public ResponseEntity<PedidoDTO> checkout(
            @Valid @RequestBody CheckoutRequest request) {
        
        Long usuarioId = obtenerUsuarioActual();
        PedidoDTO pedido = carritoService.procesarCheckout(usuarioId, request);
        return ResponseEntity.status(201).body(pedido);
    }

    /**
     * Método auxiliar para obtener el usuario actual
     * En producción, esto vendría del JWT token
     */
    private Long obtenerUsuarioActual() {
        // TODO: Extraer del JWT en SecurityConfig
        // Por ahora, simulamos usuario con ID 1
        return 1L;
    }
}