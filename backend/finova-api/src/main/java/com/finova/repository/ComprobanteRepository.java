package com.finova.repository;

import com.finova.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    Comprobante findByMovimientoId(Long movimientoId);
}
