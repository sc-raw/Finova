package com.finova.service;

import com.finova.entity.Meta;

import java.math.BigDecimal;
import java.util.List;

public interface MetaService {

    List<Meta> listarMetas();

    List<Meta> listarPorUsuario(Long usuarioId);

    Meta buscarPorId(Long id);

    Meta guardar(Meta meta);

    Meta actualizar(Long id, Meta metaActualizada);

    void eliminar(Long id);

    void aportar(Long metaId, BigDecimal monto);

    void retirar(Long metaId, BigDecimal monto);

    BigDecimal calcularProgreso(Long metaId);
}