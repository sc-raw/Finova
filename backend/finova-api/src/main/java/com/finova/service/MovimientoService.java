package com.finova.service;

import com.finova.entity.*;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final MetaRepository metaRepository;

    // ==================== CRUD ====================

    public List<Movimiento> listarMovimientos() {
        return movimientoRepository.findAll();
    }

    public List<Movimiento> listarPorUsuario(Long usuarioId) {
        return movimientoRepository.findByCuentaOrigenUsuarioIdOrderByFechaMovimientoDesc(usuarioId);
    }

    public Movimiento buscarPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Movimiento no encontrado con ID: " + id));
    }

    @Transactional
    public Movimiento guardar(Movimiento movimiento) {
        validarMovimiento(movimiento);

        Cuenta cuentaOrigen = cuentaRepository.findById(movimiento.getCuentaOrigen().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta origen no encontrada"));
        movimiento.setCuentaOrigen(cuentaOrigen);

        if (movimiento.getCuentaDestino() != null) {
            Cuenta cuentaDestino = cuentaRepository.findById(movimiento.getCuentaDestino().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta destino no encontrada"));
            movimiento.setCuentaDestino(cuentaDestino);
        }

        if (movimiento.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(movimiento.getCategoria().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada"));
            movimiento.setCategoria(categoria);
        }

        if (movimiento.getMetodoPago() != null) {
            MetodoPago metodoPago = metodoPagoRepository.findById(movimiento.getMetodoPago().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado"));
            movimiento.setMetodoPago(metodoPago);
        }

        if (movimiento.getMeta() != null) {
            Meta meta = metaRepository.findById(movimiento.getMeta().getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Meta no encontrada"));
            movimiento.setMeta(meta);
        }

        Movimiento guardado = movimientoRepository.save(movimiento);
        aplicarEfectoEnSaldos(guardado);
        return guardado;
    }

    @Transactional
    public Movimiento actualizar(Long id, Movimiento movimientoActualizado) {
        Movimiento existente = buscarPorId(id);
        revertirEfectoEnSaldos(existente);

        existente.setMonto(movimientoActualizado.getMonto());
        existente.setDescripcion(movimientoActualizado.getDescripcion());
        existente.setFechaMovimiento(movimientoActualizado.getFechaMovimiento());
        existente.setTipoMovimiento(movimientoActualizado.getTipoMovimiento());
        if (movimientoActualizado.getCategoria() != null) {
            existente.setCategoria(movimientoActualizado.getCategoria());
        }

        Movimiento guardado = movimientoRepository.save(existente);
        aplicarEfectoEnSaldos(guardado);
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Movimiento movimiento = buscarPorId(id);
        revertirEfectoEnSaldos(movimiento);
        movimientoRepository.delete(movimiento);
    }

    // ==================== LÓGICA DE SALDOS ====================

    private void aplicarEfectoEnSaldos(Movimiento movimiento) {
        BigDecimal monto = movimiento.getMonto();

        switch (movimiento.getTipoMovimiento()) {
            case GASTO:
                restarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                break;
            case INGRESO:
                sumarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                break;
            case TRANSFERENCIA:
                restarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getCuentaDestino() != null) {
                    sumarSaldoCuenta(movimiento.getCuentaDestino(), monto);
                } else {
                    throw new ReglaNegocioException("Una transferencia debe tener cuenta destino");
                }
                break;
            case APORTE_META:
                restarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getMeta() != null) {
                    sumarMontoMeta(movimiento.getMeta(), monto);
                } else {
                    throw new ReglaNegocioException("Un aporte a meta debe tener una meta asociada");
                }
                break;
            case RETIRO_META:
                sumarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getMeta() != null) {
                    restarMontoMeta(movimiento.getMeta(), monto);
                } else {
                    throw new ReglaNegocioException("Un retiro de meta debe tener una meta asociada");
                }
                break;
        }
    }

    private void revertirEfectoEnSaldos(Movimiento movimiento) {
        BigDecimal monto = movimiento.getMonto();

        switch (movimiento.getTipoMovimiento()) {
            case GASTO:
                sumarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                break;
            case INGRESO:
                restarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                break;
            case TRANSFERENCIA:
                sumarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getCuentaDestino() != null) {
                    restarSaldoCuenta(movimiento.getCuentaDestino(), monto);
                }
                break;
            case APORTE_META:
                sumarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getMeta() != null) {
                    restarMontoMeta(movimiento.getMeta(), monto);
                }
                break;
            case RETIRO_META:
                restarSaldoCuenta(movimiento.getCuentaOrigen(), monto);
                if (movimiento.getMeta() != null) {
                    sumarMontoMeta(movimiento.getMeta(), monto);
                }
                break;
        }
    }

    private void sumarSaldoCuenta(Cuenta cuenta, BigDecimal monto) {
        cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
        cuentaRepository.save(cuenta);
    }

    private void restarSaldoCuenta(Cuenta cuenta, BigDecimal monto) {
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                "Saldo insuficiente en la cuenta " + cuenta.getNombre() +
                ". Saldo: " + cuenta.getSaldoActual() + ", requerido: " + monto
            );
        }
        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }

    private void sumarMontoMeta(Meta meta, BigDecimal monto) {
        meta.setMontoActual(meta.getMontoActual().add(monto));
        metaRepository.save(meta);
    }

    private void restarMontoMeta(Meta meta, BigDecimal monto) {
        BigDecimal nuevoMonto = meta.getMontoActual().subtract(monto);
        if (nuevoMonto.compareTo(BigDecimal.ZERO) < 0) {
            nuevoMonto = BigDecimal.ZERO;
        }
        meta.setMontoActual(nuevoMonto);
        metaRepository.save(meta);
    }

    private void validarMovimiento(Movimiento movimiento) {
        if (movimiento.getMonto() == null || movimiento.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto debe ser mayor a cero");
        }
        if (movimiento.getCuentaOrigen() == null) {
            throw new ReglaNegocioException("La cuenta origen es obligatoria");
        }
        if (movimiento.getTipoMovimiento() == null) {
            throw new ReglaNegocioException("El tipo de movimiento es obligatorio");
        }
        if (movimiento.getFechaMovimiento() == null) {
            movimiento.setFechaMovimiento(LocalDateTime.now());
        }
    }

    // ==================== OPERACIONES ESPECÍFICAS ====================

    @Transactional
    public void registrarGasto(Long cuentaId, Long categoriaId, Long metodoPagoId,
                                BigDecimal monto, String descripcion) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaOrigen(cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada")));
        movimiento.setCategoria(categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada")));
        movimiento.setMetodoPago(metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado")));
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento(TipoMovimiento.GASTO);

        guardar(movimiento);
    }

    @Transactional
    public void registrarIngreso(Long cuentaId, Long categoriaId, Long metodoPagoId,
                                  BigDecimal monto, String descripcion) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaOrigen(cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada")));
        movimiento.setCategoria(categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada")));
        movimiento.setMetodoPago(metodoPagoRepository.findById(metodoPagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado")));
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento(TipoMovimiento.INGRESO);

        guardar(movimiento);
    }

    @Transactional
    public void transferir(Long cuentaOrigenId, Long cuentaDestinoId,
                           BigDecimal monto, String descripcion) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaOrigen(cuentaRepository.findById(cuentaOrigenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta origen no encontrada")));
        movimiento.setCuentaDestino(cuentaRepository.findById(cuentaDestinoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta destino no encontrada")));
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento(TipoMovimiento.TRANSFERENCIA);

        guardar(movimiento);
    }

    @Transactional
    public void aportarMeta(Long cuentaId, Long metaId, BigDecimal monto, String descripcion) {
        Movimiento movimiento = new Movimiento();
        movimiento.setCuentaOrigen(cuentaRepository.findById(cuentaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada")));
        movimiento.setMeta(metaRepository.findById(metaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Meta no encontrada")));
        movimiento.setMonto(monto);
        movimiento.setDescripcion(descripcion);
        movimiento.setFechaMovimiento(LocalDateTime.now());
        movimiento.setTipoMovimiento(TipoMovimiento.APORTE_META);

        guardar(movimiento);
    }

    // ==================== CONSULTAS ====================

    public List<Movimiento> listarPorTipoYUsuario(String tipo, Long usuarioId) {
        TipoMovimiento tipoEnum = TipoMovimiento.valueOf(tipo.toUpperCase());
        return movimientoRepository.findByTipoMovimientoAndCuentaOrigenUsuarioId(tipoEnum, usuarioId);
    }

    public List<Movimiento> listarPorCuenta(Long cuentaId) {
        return movimientoRepository.findByCuentaOrigenIdOrderByFechaMovimientoDesc(cuentaId);
    }
}