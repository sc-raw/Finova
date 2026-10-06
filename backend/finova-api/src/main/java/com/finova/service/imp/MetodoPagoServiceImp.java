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
public class MetodoPagoServiceImp implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public List<MetodoPago> listarActivos() {
        return metodoPagoRepository.findByActivoTrue();
    }

    @Override
    public MetodoPago buscarPorId(Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Método de pago no encontrado con ID: " + id
                        )
                );
    }

    @Override
    @Transactional
    public MetodoPago guardar(MetodoPago metodoPago) {

        if (metodoPago.getNombre() == null || metodoPago.getNombre().isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre del método de pago es obligatorio"
            );
        }

        if (metodoPago.getActivo() == null) {
            metodoPago.setActivo(true);
        }

        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    @Transactional
    public MetodoPago actualizar(Long id, MetodoPago actualizado) {

        MetodoPago existente = buscarPorId(id);

        existente.setNombre(actualizado.getNombre());
        existente.setActivo(actualizado.getActivo());

        return metodoPagoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {

        if (!metodoPagoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException(
                    "Método de pago no encontrado con ID: " + id
            );
        }

        metodoPagoRepository.deleteById(id);
    }
}