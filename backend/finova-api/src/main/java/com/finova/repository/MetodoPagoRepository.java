package com.finova.repository;

import com.finova.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {

    // Listar métodos de pago activos
    List<MetodoPago> findByActivoTrue();

    // Buscar por nombre
    MetodoPago findByNombre(String nombre);
}