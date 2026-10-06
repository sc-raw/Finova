package com.finova.service;

import com.finova.entity.MetodoPago;

import java.util.List;

public interface MetodoPagoService {

    List<MetodoPago> listarMetodosPago();

    List<MetodoPago> listarActivos();

    MetodoPago buscarPorId(Long id);

    MetodoPago guardar(MetodoPago metodoPago);

    MetodoPago actualizar(Long id, MetodoPago metodoPagoActualizado);

    void eliminar(Long id);
}