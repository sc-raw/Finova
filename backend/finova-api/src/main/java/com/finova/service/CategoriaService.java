package com.finova.service;

import com.finova.entity.Categoria;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> listarPorTipo(String tipo) {
        return categoriaRepository.findByTipo(tipo);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id));
    }

    @Transactional
    public Categoria guardar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre de la categoría es obligatorio");
        }
        if (categoria.getTipo() == null || categoria.getTipo().isBlank()) {
            throw new ReglaNegocioException("El tipo de categoría es obligatorio (GASTO o INGRESO)");
        }
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizar(Long id, Categoria categoriaActualizada) {
        Categoria existente = buscarPorId(id);
        existente.setNombre(categoriaActualizada.getNombre());
        existente.setTipo(categoriaActualizada.getTipo());
        return categoriaRepository.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}