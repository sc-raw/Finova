package com.finova.service;

import com.finova.entity.Comprobante;

import java.util.List;

public interface ComprobanteService {

    List<Comprobante> listarComprobantes();

    Comprobante buscarPorId(Long id);

    Comprobante guardar(Comprobante comprobante);

    Comprobante actualizar(Long id, Comprobante comprobanteActualizado);

    void eliminar(Long id);

    Comprobante buscarPorMovimiento(Long movimientoId);
}