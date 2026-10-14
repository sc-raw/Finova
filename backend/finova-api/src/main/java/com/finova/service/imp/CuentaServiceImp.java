
package com.finova.service.imp;

import com.finova.entity.Cuenta;
import com.finova.entity.Usuario;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CuentaRepository;
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
