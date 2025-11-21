package com.tienda.carrito.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carrito_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    

    @Column(nullable = false)
    private Long productoId;
    
    @Column(nullable = false)
    private Integer cantidad;

    private Double precioAlAgregar;
    
    public Double calcularSubtotal() {
        return precioAlAgregar != null ? precioAlAgregar * cantidad : 0.0;
    }
}