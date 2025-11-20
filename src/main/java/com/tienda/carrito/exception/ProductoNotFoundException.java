package com.tienda.carrito.exception;

public class ProductoNotFoundException extends RuntimeException {
    public ProductoNotFoundException(String mensaje) {
        super(mensaje);
    }
}