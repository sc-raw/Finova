
package com.finova.service.imp;

import com.finova.entity.Meta;
import com.finova.entity.Usuario;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.MetaRepository;
import com.finova.repository.UsuarioRepository;
import com.finova.service.MetaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MetaServiceImp implements MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Meta> listarMetas() {
        return metaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Meta> listarPorUsuario(Long usuarioId) {
        validarId(usuarioId, "usuario");
        return metaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public Meta buscarPorId(Long id) {
        validarId(id, "meta");

        return metaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Meta no encontrada con ID: " + id
                        )
                );
    }

    @Override
    public Meta guardar(Meta meta) {
        validarMeta(meta);

        if (meta.getUsuario() == null ||
                meta.getUsuario().getId() == null) {
            throw new ReglaNegocioException(
                    "La meta debe estar asociada a un usuario"
            );
        }

        Usuario usuario = usuarioRepository
                .findById(meta.getUsuario().getId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario no encontrado"
                        )
                );

        BigDecimal montoActual = meta.getMontoActual() == null
                ? BigDecimal.ZERO
                : meta.getMontoActual();

        validarMontoNoNegativo(montoActual, "El monto actual");

        if (montoActual.compareTo(meta.getMontoObjetivo()) > 0) {
            throw new ReglaNegocioException(
                    "El monto actual no puede superar al objetivo"
            );
        }

        meta.setNombre(meta.getNombre().trim());
        meta.setMontoActual(montoActual);
        meta.setUsuario(usuario);

        return metaRepository.save(meta);
    }

    @Override
    public Meta actualizar(Long id, Meta metaActualizada) {
        validarId(id, "meta");

        if (metaActualizada == null) {
            throw new ReglaNegocioException(
                    "Los datos de la meta son obligatorios"
            );
        }

        Meta existente = buscarPorId(id);

        if (metaActualizada.getNombre() == null ||
                metaActualizada.getNombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre de la meta es obligatorio"
            );
        }

        BigDecimal objetivo = metaActualizada.getMontoObjetivo();

        if (objetivo == null ||
                objetivo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto objetivo debe ser mayor que cero"
            );
        }

        if (existente.getMontoActual() != null &&
                existente.getMontoActual().compareTo(objetivo) > 0) {
            throw new ReglaNegocioException(
                    "El monto objetivo no puede ser menor que lo ahorrado"
            );
        }

        existente.setNombre(metaActualizada.getNombre().trim());
        existente.setMontoObjetivo(objetivo);
        existente.setFechaObjetivo(metaActualizada.getFechaObjetivo());

        return metaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Meta meta = buscarPorId(id);
        metaRepository.delete(meta);
    }

    @Override
    public void aportar(Long metaId, BigDecimal monto) {
        validarMontoPositivo(monto);

        Meta meta = buscarPorId(metaId);
        BigDecimal actual = obtenerMontoActual(meta);
        BigDecimal nuevoMonto = actual.add(monto);

        if (nuevoMonto.compareTo(meta.getMontoObjetivo()) > 0) {
            throw new ReglaNegocioException(
                    "El aporte supera el monto objetivo de la meta"
            );
        }

        meta.setMontoActual(nuevoMonto);
        metaRepository.save(meta);
    }

    @Override
    public void retirar(Long metaId, BigDecimal monto) {
        validarMontoPositivo(monto);

        Meta meta = buscarPorId(metaId);
        BigDecimal actual = obtenerMontoActual(meta);
        BigDecimal nuevoMonto = actual.subtract(monto);

        if (nuevoMonto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "No se puede retirar más de lo ahorrado"
            );
        }

        meta.setMontoActual(nuevoMonto);
        metaRepository.save(meta);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularProgreso(Long metaId) {
        Meta meta = buscarPorId(metaId);

        BigDecimal objetivo = meta.getMontoObjetivo();
        BigDecimal actual = obtenerMontoActual(meta);

        if (objetivo == null ||
                objetivo.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return actual.multiply(new BigDecimal("100"))
                .divide(objetivo, 2, RoundingMode.HALF_UP);
    }

    private void validarMeta(Meta meta) {
        if (meta == null) {
            throw new ReglaNegocioException(
                    "Los datos de la meta son obligatorios"
            );
        }

        if (meta.getNombre() == null ||
                meta.getNombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre de la meta es obligatorio"
            );
        }

        if (meta.getMontoObjetivo() == null ||
                meta.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto objetivo debe ser mayor que cero"
            );
        }
    }

    private BigDecimal obtenerMontoActual(Meta meta) {
        if (meta.getMontoActual() == null) {
            return BigDecimal.ZERO;
        }

        return meta.getMontoActual();
    }

    private void validarMontoPositivo(BigDecimal monto) {
        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException(
                    "El monto debe ser mayor que cero"
            );
        }
    }

    private void validarMontoNoNegativo(
            BigDecimal monto,
            String campo) {
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    campo + " no puede ser negativo"
            );
        }
    }

    private void validarId(Long id, String entidad) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID de " + entidad + " debe ser válido"
            );
        }
    }
}
