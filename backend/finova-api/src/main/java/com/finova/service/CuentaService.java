package com.finova.service;

import com.finova.entity.Cuenta;
import com.finova.entity.Usuario;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CuentaRepository;
import com.finova.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;

    // ==================== CRUD ====================

    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public List<Cuenta> listarPorUsuario(Long usuarioId) {
        return cuentaRepository.findByUsuarioId(usuarioId);
    }

    public Cuenta buscarPorId(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuenta no encontrada con ID: " + id));
    }

    @Transactional
    public Cuenta guardar(Cuenta cuenta) {
        // Validar que el usuario exista
        Usuario usuario = usuarioRepository.findById(cuenta.getUsuario().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        // Validar que el número de cuenta sea único
        // (Si necesitas esta validación, agrega un método en el repo)

        // Validar saldos
        if (cuenta.getSaldoInicial() == null) {
            cuenta.setSaldoInicial(BigDecimal.ZERO);
        }
        if (cuenta.getSaldoActual() == null) {
            cuenta.setSaldoActual(cuenta.getSaldoInicial());
        }

        cuenta.setUsuario(usuario);
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public Cuenta actualizar(Long id, Cuenta cuentaActualizada) {
        Cuenta existente = buscarPorId(id);
        existente.setNombre(cuentaActualizada.getNombre());
        existente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
        existente.setBanco(cuentaActualizada.getBanco());
        existente.setMoneda(cuentaActualizada.getMoneda());
        return cuentaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Cuenta cuenta = buscarPorId(id);

        // Validar que no tenga saldo (regla de negocio opcional)
        if (cuenta.getSaldoActual().compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException(
                "No se puede eliminar una cuenta con saldo. Saldo actual: " + cuenta.getSaldoActual()
            );
        }
        cuentaRepository.delete(cuenta);
    }

    // ==================== OPERACIONES ====================

    @Transactional
    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo) {
        Cuenta cuenta = buscarPorId(cuentaId);
        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }

    @Transactional
    public void sumarSaldo(Long cuentaId, BigDecimal monto) {
        Cuenta cuenta = buscarPorId(cuentaId);
        cuenta.setSaldoActual(cuenta.getSaldoActual().add(monto));
        cuentaRepository.save(cuenta);
    }

    @Transactional
    public void restarSaldo(Long cuentaId, BigDecimal monto) {
        Cuenta cuenta = buscarPorId(cuentaId);
        BigDecimal nuevoSaldo = cuenta.getSaldoActual().subtract(monto);

        // Validar saldo suficiente (opcional, depende de la lógica de negocio)
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                "Saldo insuficiente en la cuenta " + cuenta.getNombre() +
                ". Saldo disponible: " + cuenta.getSaldoActual()
            );
        }
        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
}