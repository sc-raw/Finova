package com.finova.service.imp;

import com.finova.entity.Comprobante;
import com.finova.entity.Movimiento;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.ComprobanteRepository;
import com.finova.repository.MovimientoRepository;
import com.finova.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ComprobanteServiceImp implements ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public List<Comprobante> listarComprobantes() {
        return comprobanteRepository.findAll();
    }

    @Override
    public Comprobante buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID del comprobante debe ser válido"
            );
        }

        return comprobanteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Comprobante no encontrado con ID: " + id
                        )
                );
    }

    @Override
    @Transactional
    public Comprobante guardar(Comprobante comprobante) {
        if (comprobante == null) {
            throw new ReglaNegocioException(
                    "Los datos del comprobante son obligatorios"
            );
        }

        if (comprobante.getMovimiento() == null ||
                comprobante.getMovimiento().getId() == null) {
            throw new ReglaNegocioException(
                    "El comprobante debe estar asociado a un movimiento"
            );
        }

        Long movimientoId = comprobante.getMovimiento().getId();

        Movimiento movimiento = movimientoRepository.findById(movimientoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Movimiento no encontrado con ID: " + movimientoId
                        )
                );

        if (comprobanteRepository.findByMovimientoId(movimientoId) != null) {
            throw new ReglaNegocioException(
                    "El movimiento ya tiene un comprobante asociado"
            );
        }

        comprobante.setMovimiento(movimiento);

        return comprobanteRepository.save(comprobante);
    }

    @Override
    @Transactional
    public Comprobante actualizar(Long id, Comprobante actualizado) {
        if (actualizado == null) {
            throw new ReglaNegocioException(
                    "Los datos del comprobante son obligatorios"
            );
        }

        Comprobante existente = buscarPorId(id);

        existente.setNombreComercio(actualizado.getNombreComercio());
        existente.setRuc(actualizado.getRuc());
        existente.setCodigoOperacion(actualizado.getCodigoOperacion());
        existente.setRutaComprobante(actualizado.getRutaComprobante());

        return comprobanteRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Comprobante existente = buscarPorId(id);
        comprobanteRepository.delete(existente);
    }

    @Override
    public Comprobante buscarPorMovimiento(Long movimientoId) {
        if (movimientoId == null || movimientoId <= 0) {
            throw new ReglaNegocioException(
                    "El ID del movimiento debe ser válido"
            );
        }

        Comprobante comprobante =
                comprobanteRepository.findByMovimientoId(movimientoId);

        if (comprobante == null) {
            throw new RecursoNoEncontradoException(
                    "No se encontró un comprobante para el movimiento con ID: "
                            + movimientoId
            );
        }

        return comprobante;
    }
}
