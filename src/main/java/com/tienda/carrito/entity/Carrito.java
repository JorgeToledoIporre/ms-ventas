package com.tienda.carrito.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long usuarioId;
    
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarritoItem> items = new ArrayList<>();
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
    
    // --- MÉTODOS DE NEGOCIO ---
    public Double calcularSubtotal() {
        return items.stream()
            .mapToDouble(item -> (item.getPrecioAlAgregar() != null ? item.getPrecioAlAgregar() : 0.0) * item.getCantidad())
            .sum();
    }
    
    public Integer contarTotalItems() {
        return items.stream()
            .mapToInt(CarritoItem::getCantidad)
            .sum();
    }
    
    public void agregarItem(CarritoItem item) {
        items.add(item);
        item.setCarrito(this);
    }
    
    public void eliminarItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
    }
}