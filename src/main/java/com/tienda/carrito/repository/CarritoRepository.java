package com.tienda.carrito.repository;

import com.tienda.carrito.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    
    Optional<Carrito> findByUsuarioIdAndActivoTrue(Long usuarioId);
    
    List<Carrito> findByActivoTrueAndFechaActualizacionBefore(LocalDateTime fecha);
    
    boolean existsByUsuarioIdAndActivoTrue(Long usuarioId);
}