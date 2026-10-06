package com.finova.service;

import com.finova.entity.Movimiento;

import java.math.BigDecimal;
import java.util.List;

public interface MovimientoService {

    List<Movimiento> listarMovimientos();

    List<Movimiento> listarPorUsuario(Long usuarioId);

    Movimiento buscarPorId(Long id);

    Movimiento guardar(Movimiento movimiento);

    Movimiento actualizar(Long id, Movimiento movimiento);

    void eliminar(Long id);

    void registrarGasto(
            Long cuentaId,
            Long categoriaId,
            Long metodoPagoId,
            BigDecimal monto,
            String descripcion
    );

    void registrarIngreso(
            Long cuentaId,
            Long categoriaId,
            Long metodoPagoId,
            BigDecimal monto,
            String descripcion
    );

    void transferir(
            Long cuentaOrigenId,
            Long cuentaDestinoId,
            BigDecimal monto,
            String descripcion
    );

    void aportarMeta(
            Long cuentaId,
            Long metaId,
            BigDecimal monto,
            String descripcion
    );

    List<Movimiento> listarPorTipoYUsuario(
            String tipo,
            Long usuarioId
    );

    List<Movimiento> listarPorCuenta(Long cuentaId);
}