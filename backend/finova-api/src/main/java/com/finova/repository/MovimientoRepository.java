
package com.finova.repository;

import com.finova.entity.Movimiento;
import com.finova.entity.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(
            Long usuarioId
    );

    List<Movimiento> findByCuentaOrigenIdOrderByFechaMovimientoDesc(
            Long cuentaId
    );

    List<Movimiento> findByTipoMovimientoAndCuentaOrigenUsuarioId(
            TipoMovimiento tipo,
            Long usuarioId
    );
}