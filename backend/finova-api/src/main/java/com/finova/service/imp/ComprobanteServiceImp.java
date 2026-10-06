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
public class ComprobanteServiceImp implements ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final MovimientoRepository movimientoRepository;

    @Override
    public List<Comprobante> listarComprobantes() {
        return comprobanteRepository.findAll();
    }

    @Override
    public Comprobante buscarPorId(Long id) {
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

        if (comprobante.getMovimiento() == null ||
                comprobante.getMovimiento().getId() == null) {

            throw new ReglaNegocioException(
                    "El comprobante debe estar asociado a un movimiento"
            );
        }

        Movimiento movimiento = movimientoRepository
                .findById(comprobante.getMovimiento().getId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Movimiento no encontrado"
                        )
                );

        Comprobante existente =
                comprobanteRepository.findByMovimientoId(movimiento.getId());

        if (existente != null) {
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

        if (!comprobanteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Comprobante no encontrado con ID: " + id
            );
        }

        comprobanteRepository.deleteById(id);
    }

    @Override
    public Comprobante buscarPorMovimiento(Long movimientoId) {
        return comprobanteRepository.findByMovimientoId(movimientoId);
    }
}