package com.finova.service;

import com.finova.entity.Meta;
import com.finova.entity.Usuario;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.MetaRepository;
import com.finova.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    // ==================== CRUD ====================

    public List<Meta> listarMetas() {
        return metaRepository.findAll();
    }

    public List<Meta> listarPorUsuario(Long usuarioId) {
        return metaRepository.findByUsuarioId(usuarioId);
    }

    public Meta buscarPorId(Long id) {
        return metaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Meta no encontrada con ID: " + id));
    }

    @Transactional
    public Meta guardar(Meta meta) {
        // Validaciones
        if (meta.getMontoObjetivo() == null || meta.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El monto objetivo debe ser mayor a cero");
        }
        if (meta.getMontoActual() == null) {
            meta.setMontoActual(BigDecimal.ZERO);
        }
        if (meta.getMontoActual().compareTo(meta.getMontoObjetivo()) > 0) {
            throw new ReglaNegocioException("El monto actual no puede superar al objetivo");
        }

        // Validar usuario
        Usuario usuario = usuarioRepository.findById(meta.getUsuario().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        meta.setUsuario(usuario);

        return metaRepository.save(meta);
    }

    @Transactional
    public Meta actualizar(Long id, Meta metaActualizada) {
        Meta existente = buscarPorId(id);
        existente.setNombre(metaActualizada.getNombre());
        existente.setMontoObjetivo(metaActualizada.getMontoObjetivo());
        existente.setFechaObjetivo(metaActualizada.getFechaObjetivo());
        return metaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!metaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Meta no encontrada con ID: " + id);
        }
        metaRepository.deleteById(id);
    }

    // ==================== OPERACIONES ====================

    @Transactional
    public void aportar(Long metaId, BigDecimal monto) {
        Meta meta = buscarPorId(metaId);
        BigDecimal nuevoMonto = meta.getMontoActual().add(monto);
        meta.setMontoActual(nuevoMonto);
        metaRepository.save(meta);
    }

    @Transactional
    public void retirar(Long metaId, BigDecimal monto) {
        Meta meta = buscarPorId(metaId);
        BigDecimal nuevoMonto = meta.getMontoActual().subtract(monto);
        if (nuevoMonto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException("No se puede retirar más de lo ahorrado");
        }
        meta.setMontoActual(nuevoMonto);
        metaRepository.save(meta);
    }

    public BigDecimal calcularProgreso(Long metaId) {
        Meta meta = buscarPorId(metaId);
        if (meta.getMontoObjetivo().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return meta.getMontoActual()
                .multiply(new BigDecimal("100"))
                .divide(meta.getMontoObjetivo(), 2, BigDecimal.ROUND_HALF_UP);
    }
}