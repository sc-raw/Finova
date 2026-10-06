package com.finova.service;

import com.finova.entity.Categoria;

import java.util.List;

public interface CategoriaService {

    List<Categoria> listarCategorias();

    List<Categoria> listarPorTipo(String tipo);

    Categoria buscarPorId(Long id);

    Categoria guardar(Categoria categoria);

    Categoria actualizar(Long id, Categoria categoriaActualizada);

    void eliminar(Long id);
}