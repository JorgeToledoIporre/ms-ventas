package com.tienda.carrito.repository;

import com.tienda.carrito.entity.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    
    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
    
    boolean existsByCarritoIdAndProductoId(Long carritoId, Long productoId);
}