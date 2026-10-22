package com.finova.service;

import com.finova.entity.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    List<Cuenta> listarCuentas();

    List<Cuenta> listarPorUsuario(Long usuarioId);

    Cuenta buscarPorId(Long id);

    Cuenta guardar(Cuenta cuenta);

    Cuenta actualizar(Long id, Cuenta cuentaActualizada);

    void eliminar(Long id);

    void eliminarSinSaldo(Long id);

    void eliminarConTransferencia(Long id, Long cuentaDestinoId);

    void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo);

    void sumarSaldo(Long cuentaId, BigDecimal monto);

    void restarSaldo(Long cuentaId, BigDecimal monto);
}