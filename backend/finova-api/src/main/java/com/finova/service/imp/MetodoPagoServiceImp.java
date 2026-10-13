
package com.finova.service.imp;

import com.finova.entity.MetodoPago;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.MetodoPagoRepository;
import com.finova.service.MetodoPagoService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MetodoPagoServiceImp implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPago> listarActivos() {
        return metodoPagoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPago buscarPorId(Long id) {
        validarId(id);

        return metodoPagoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: " + id
                        )
                );
    }

    @Override
    public MetodoPago guardar(MetodoPago metodoPago) {
        validarMetodoPago(metodoPago);

        metodoPago.setNombre(metodoPago.getNombre().trim());

        if (metodoPago.getActivo() == null) {
            metodoPago.setActivo(true);
        }

        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public MetodoPago actualizar(Long id, MetodoPago actualizado) {
        validarId(id);
        validarMetodoPago(actualizado);

        MetodoPago existente = buscarPorId(id);

        existente.setNombre(actualizado.getNombre().trim());

        if (actualizado.getActivo() != null) {
            existente.setActivo(actualizado.getActivo());
        }

        return metodoPagoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        MetodoPago metodoPago = buscarPorId(id);
        metodoPagoRepository.delete(metodoPago);
    }

    private void validarMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == null) {
            throw new ReglaNegocioException(
                    "Los datos del método de pago son obligatorios"
            );
        }

        if (metodoPago.getNombre() == null ||
                metodoPago.getNombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre del método de pago es obligatorio"
            );
        }
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID del método de pago debe ser válido"
            );
        }
    }
}
