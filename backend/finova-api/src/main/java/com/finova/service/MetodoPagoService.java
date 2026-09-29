package com.finova.service;

import com.finova.entity.MetodoPago;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.MetodoPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    public List<MetodoPago> listarMetodosPago() {
        return metodoPagoRepository.findAll();
    }

    public List<MetodoPago> listarActivos() {
        return metodoPagoRepository.findByActivoTrue();
    }

    public MetodoPago buscarPorId(Long id) {
        return metodoPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Método de pago no encontrado con ID: " + id));
    }

    @Transactional
    public MetodoPago guardar(MetodoPago metodoPago) {
        if (metodoPago.getNombre() == null || metodoPago.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre del método de pago es obligatorio");
        }
        if (metodoPago.getActivo() == null) {
            metodoPago.setActivo(true);
        }
        return metodoPagoRepository.save(metodoPago);
    }

    @Transactional
    public MetodoPago actualizar(Long id, MetodoPago actualizado) {
        MetodoPago existente = buscarPorId(id);
        existente.setNombre(actualizado.getNombre());
        existente.setActivo(actualizado.getActivo());
        return metodoPagoRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!metodoPagoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Método de pago no encontrado con ID: " + id);
        }
        metodoPagoRepository.deleteById(id);
    }
}