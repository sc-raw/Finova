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
public class MetaServiceImp implements MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<Meta> listarMetas() {
        return metaRepository.findAll();
    }

    @Override
    public List<Meta> listarPorUsuario(Long usuarioId) {
        return metaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Meta buscarPorId(Long id) {
        return metaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Meta no encontrada con ID: " + id
                        )
                );
    }

    @Override
    @Transactional
    public Meta guardar(Meta meta) {

        if (meta.getMontoObjetivo() == null ||
                meta.getMontoObjetivo().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ReglaNegocioException(
                    "El monto objetivo debe ser mayor a cero"
            );
        }

        if (meta.getMontoActual() == null) {
            meta.setMontoActual(BigDecimal.ZERO);
        }

        if (meta.getMontoActual()
                .compareTo(meta.getMontoObjetivo()) > 0) {

            throw new ReglaNegocioException(
                    "El monto actual no puede superar al objetivo"
            );
        }

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

        meta.setUsuario(usuario);

        return metaRepository.save(meta);
    }

    @Override
    @Transactional
    public Meta actualizar(Long id, Meta metaActualizada) {

        Meta existente = buscarPorId(id);

        existente.setNombre(metaActualizada.getNombre());
        existente.setMontoObjetivo(metaActualizada.getMontoObjetivo());
        existente.setFechaObjetivo(metaActualizada.getFechaObjetivo());

        return metaRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        if (!metaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Meta no encontrada con ID: " + id
            );
        }

        metaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void aportar(Long metaId, BigDecimal monto) {

        Meta meta = buscarPorId(metaId);

        BigDecimal nuevoMonto =
                meta.getMontoActual().add(monto);

        meta.setMontoActual(nuevoMonto);

        metaRepository.save(meta);
    }

    @Override
    @Transactional
    public void retirar(Long metaId, BigDecimal monto) {

        Meta meta = buscarPorId(metaId);

        BigDecimal nuevoMonto =
                meta.getMontoActual().subtract(monto);

        if (nuevoMonto.compareTo(BigDecimal.ZERO) < 0) {
            throw new ReglaNegocioException(
                    "No se puede retirar más de lo ahorrado"
            );
        }

        meta.setMontoActual(nuevoMonto);

        metaRepository.save(meta);
    }

    @Override
    public BigDecimal calcularProgreso(Long metaId) {

        Meta meta = buscarPorId(metaId);

        if (meta.getMontoObjetivo()
                .compareTo(BigDecimal.ZERO) == 0) {

            return BigDecimal.ZERO;
        }

        return meta.getMontoActual()
                .multiply(new BigDecimal("100"))
                .divide(
                        meta.getMontoObjetivo(),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}