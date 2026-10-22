
package com.finova.service.imp;

import com.finova.entity.Cuenta;
import com.finova.entity.Usuario;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CuentaRepository;
import com.finova.repository.MovimientoRepository;
import com.finova.entity.Movimiento;
import com.finova.service.MovimientoService;
import com.finova.repository.UsuarioRepository;
import com.finova.service.CuentaService;
import com.finova.entity.Banco;
import com.finova.repository.BancoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaServiceImp implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final BancoRepository bancoRepository;
    private final MovimientoRepository movimientoRepository;
    private final MovimientoService movimientoService;

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarCuentas() {
        return cuentaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cuenta> listarPorUsuario(Long usuarioId) {
        validarId(usuarioId, "usuario");
        return cuentaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Cuenta buscarPorId(Long id) {
        validarId(id, "cuenta");

        return cuentaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Cuenta no encontrada con ID: " + id
                        )
                );
    }


	@Override
	public Cuenta guardar(Cuenta cuenta) {
	    if (cuenta == null) {
	        throw new ReglaNegocioException("La cuenta es obligatoria");
	    }
	
	    if (cuenta.getUsuario() == null ||
	            cuenta.getUsuario().getId() == null) {
	        throw new ReglaNegocioException(
	                "El usuario de la cuenta es obligatorio"
	        );
	    }
	
	    if (cuenta.getBanco() == null ||
	            cuenta.getBanco().getId() == null) {
	        throw new ReglaNegocioException(
	                "El banco de la cuenta es obligatorio"
	        );
	    }
	
	    if (cuenta.getNombre() == null ||
	            cuenta.getNombre().isBlank()) {
	        throw new ReglaNegocioException(
	                "El nombre de la cuenta es obligatorio"
	        );
	    }
	
	    Usuario usuario = usuarioRepository.findById(
	            cuenta.getUsuario().getId()
	    ).orElseThrow(() ->
	            new RecursoNoEncontradoException(
	                    "Usuario no encontrado"
	            )
	    );
	
	    Banco banco = bancoRepository.findById(
	            cuenta.getBanco().getId()
	    ).orElseThrow(() ->
	            new RecursoNoEncontradoException(
	                    "Banco no encontrado"
	            )
	    );
	
	    BigDecimal saldoInicial = cuenta.getSaldoInicial() == null
	            ? BigDecimal.ZERO
	            : cuenta.getSaldoInicial();
	
	    if (saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
	        throw new ReglaNegocioException(
	                "El saldo inicial no puede ser negativo"
	        );
	    }
	
	    cuenta.setSaldoInicial(saldoInicial);
	
	    if (cuenta.getSaldoActual() == null) {
	        cuenta.setSaldoActual(saldoInicial);
	    }
	
	    if (cuenta.getSaldoActual().compareTo(BigDecimal.ZERO) < 0) {
	        throw new ReglaNegocioException(
	                "El saldo actual no puede ser negativo"
	        );
	    }
	
	    cuenta.setUsuario(usuario);
	    cuenta.setBanco(banco);
	
	    return cuentaRepository.save(cuenta);
	}
	
	@Override
	public Cuenta actualizar(Long id, Cuenta cuentaActualizada) {
	    validarId(id, "cuenta");
	
	    if (cuentaActualizada == null) {
	        throw new ReglaNegocioException(
	                "Los datos de la cuenta son obligatorios"
	        );
	    }
	
	    Cuenta existente = buscarPorId(id);
	
	    if (cuentaActualizada.getNombre() == null ||
	            cuentaActualizada.getNombre().isBlank()) {
	        throw new ReglaNegocioException(
	                "El nombre de la cuenta es obligatorio"
	        );
	    }
	
	    if (cuentaActualizada.getBanco() == null ||
	            cuentaActualizada.getBanco().getId() == null) {
	        throw new ReglaNegocioException(
	                "El banco de la cuenta es obligatorio"
	        );
	    }
	
	    Banco banco = bancoRepository.findById(
	            cuentaActualizada.getBanco().getId()
	    ).orElseThrow(() ->
	            new RecursoNoEncontradoException(
	                    "Banco no encontrado"
	            )
	    );
	
	    existente.setNombre(cuentaActualizada.getNombre().trim());
	    existente.setNumeroCuenta(cuentaActualizada.getNumeroCuenta());
	    existente.setBanco(banco);
	    existente.setMoneda(cuentaActualizada.getMoneda());
	
	    return cuentaRepository.save(existente);
	}


    @Override
    public void eliminar(Long id) {
        Cuenta cuenta = buscarPorId(id);

        BigDecimal saldo = cuenta.getSaldoActual();

        if (saldo == null) {
            throw new ReglaNegocioException(
                    "La cuenta no tiene un saldo actual válido"
            );
        }

        if (saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException(
                    "No se puede eliminar una cuenta con saldo. " +
                    "Saldo actual: " + saldo
            );
        }

        cuentaRepository.delete(cuenta);
    }

    @Override
    @Transactional
    public void eliminarSinSaldo(Long id) {
        validarId(id, "cuenta");
        Cuenta cuenta = buscarPorId(id);
        Long usuarioId = cuenta.getUsuario().getId();
        List<Cuenta> cuentasUsuario = cuentaRepository.findByUsuarioId(usuarioId);
        if (cuentasUsuario.size() <= 1) {
            throw new ReglaNegocioException("No puedes eliminar la única cuenta registrada");
        }
        Long principalId = cuentasUsuario.stream().map(Cuenta::getId).min(Long::compareTo).orElse(null);
        if (id.equals(principalId)) {
            throw new ReglaNegocioException("No puedes eliminar la cuenta principal");
        }
        BigDecimal saldo = cuenta.getSaldoActual();
        if (saldo == null || saldo.compareTo(BigDecimal.ZERO) != 0) {
            throw new ReglaNegocioException("Solo puedes eliminar una cuenta cuando su saldo sea exactamente cero");
        }

        List<Movimiento> movimientos = movimientoRepository.findAll().stream()
                .filter(m -> (m.getCuentaOrigen() != null && id.equals(m.getCuentaOrigen().getId()))
                        || (m.getCuentaDestino() != null && id.equals(m.getCuentaDestino().getId())))
                .toList();
        for (Movimiento movimiento : movimientos) {
            String nota = "Cuenta eliminada: " + cuenta.getNombre();
            if (movimiento.getDescripcion() == null || !movimiento.getDescripcion().contains(nota)) {
                movimiento.setDescripcion((movimiento.getDescripcion() == null || movimiento.getDescripcion().isBlank())
                        ? nota : movimiento.getDescripcion() + " | " + nota);
            }
            if (movimiento.getCuentaOrigen() != null && id.equals(movimiento.getCuentaOrigen().getId())) {
                movimiento.setCuentaOrigen(null);
            }
            if (movimiento.getCuentaDestino() != null && id.equals(movimiento.getCuentaDestino().getId())) {
                movimiento.setCuentaDestino(null);
            }
        }
        movimientoRepository.saveAll(movimientos);
        cuentaRepository.delete(cuenta);
    }

    @Override
    @Transactional
    public void eliminarConTransferencia(Long id, Long cuentaDestinoId) {
        validarId(id, "cuenta");
        validarId(cuentaDestinoId, "cuenta destino");
        if (id.equals(cuentaDestinoId)) {
            throw new ReglaNegocioException("La cuenta de destino debe ser diferente");
        }

        Cuenta origen = buscarPorId(id);
        Cuenta destino = buscarPorId(cuentaDestinoId);
        Long usuarioId = origen.getUsuario().getId();
        if (!destino.getUsuario().getId().equals(usuarioId)) {
            throw new ReglaNegocioException("La cuenta de destino debe pertenecer al mismo usuario");
        }
        if (origen.getMoneda() != destino.getMoneda()) {
            throw new ReglaNegocioException("La cuenta de destino debe usar la misma moneda");
        }

        List<Cuenta> cuentasUsuario = cuentaRepository.findByUsuarioId(usuarioId);
        if (cuentasUsuario.size() <= 1) {
            throw new ReglaNegocioException("No puedes eliminar la única cuenta registrada");
        }
        Long principalId = cuentasUsuario.stream().map(Cuenta::getId).min(Long::compareTo).orElse(null);
        if (id.equals(principalId)) {
            throw new ReglaNegocioException("No puedes eliminar la cuenta principal");
        }

        BigDecimal saldo = origen.getSaldoActual() == null ? BigDecimal.ZERO : origen.getSaldoActual();
        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("El saldo de la cuenta no es válido");
        }
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            movimientoService.transferir(id, cuentaDestinoId, saldo,
                    "Transferencia por eliminación de cuenta: " + origen.getNombre());
        }

        // Conserva el historial de movimientos, pero elimina las referencias FK
        // a la cuenta que dejará de existir. El texto conserva el nombre de la cuenta.
        List<Movimiento> movimientos = movimientoRepository.findAll().stream()
                .filter(m -> (m.getCuentaOrigen() != null && id.equals(m.getCuentaOrigen().getId()))
                        || (m.getCuentaDestino() != null && id.equals(m.getCuentaDestino().getId())))
                .toList();
        for (Movimiento movimiento : movimientos) {
            String nota = "Cuenta eliminada: " + origen.getNombre();
            if (movimiento.getDescripcion() == null || !movimiento.getDescripcion().contains(nota)) {
                movimiento.setDescripcion((movimiento.getDescripcion() == null || movimiento.getDescripcion().isBlank())
                        ? nota : movimiento.getDescripcion() + " | " + nota);
            }
            if (movimiento.getCuentaOrigen() != null && id.equals(movimiento.getCuentaOrigen().getId())) {
                movimiento.setCuentaOrigen(null);
            }
            if (movimiento.getCuentaDestino() != null && id.equals(movimiento.getCuentaDestino().getId())) {
                movimiento.setCuentaDestino(null);
            }
        }
        movimientoRepository.saveAll(movimientos);
        cuentaRepository.delete(origen);
    }

    @Override
    public void actualizarSaldo(Long cuentaId, BigDecimal nuevoSaldo) {
        Cuenta cuenta = buscarPorId(cuentaId);

        if (nuevoSaldo == null) {
            throw new ReglaNegocioException(
                    "El nuevo saldo es obligatorio"
            );
        }

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "El saldo no puede ser negativo"
            );
        }

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }

    @Override
    public void sumarSaldo(Long cuentaId, BigDecimal monto) {
        validarMonto(monto);

        Cuenta cuenta = buscarPorId(cuentaId);

        BigDecimal saldoActual = obtenerSaldoActual(cuenta);
        cuenta.setSaldoActual(saldoActual.add(monto));

        cuentaRepository.save(cuenta);
    }

    @Override
    public void restarSaldo(Long cuentaId, BigDecimal monto) {
        validarMonto(monto);

        Cuenta cuenta = buscarPorId(cuentaId);
        BigDecimal saldoActual = obtenerSaldoActual(cuenta);

        BigDecimal nuevoSaldo = saldoActual.subtract(monto);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "Saldo insuficiente en la cuenta " +
                    cuenta.getNombre() +
                    ". Saldo disponible: " + saldoActual
            );
        }

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }

    private void validarId(Long id, String entidad) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID de " + entidad + " debe ser válido"
            );
        }
    }

    private void validarMonto(BigDecimal monto) {
        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto debe ser mayor que cero"
            );
        }

        if (monto.scale() > 2) {
            throw new ReglaNegocioException(
                    "El monto no puede tener más de dos decimales"
            );
        }
    }

    private BigDecimal obtenerSaldoActual(Cuenta cuenta) {
        if (cuenta.getSaldoActual() == null) {
            throw new ReglaNegocioException(
                    "La cuenta no tiene un saldo actual válido"
            );
        }

        return cuenta.getSaldoActual();
    }
}
