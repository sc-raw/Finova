package com.finova.repository;

import com.finova.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    // Buscar comprobante por movimiento
    Comprobante findByMovimientoId(Long movimientoId);

    // ==================== SP ====================

    // SP: Obtener documento de una transacción
    @Procedure(name = "sp_obtener_documento")
    List<Object[]> obtenerDocumento(@Param("p_transaction_id") Integer transactionId);
}