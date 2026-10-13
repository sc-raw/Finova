
package com.finova.service.imp;

import com.finova.entity.Categoria;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.CategoriaRepository;
import com.finova.service.CategoriaService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoriaServiceImp implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> listarPorTipo(String tipo) {
        validarTipo(tipo);

        return categoriaRepository.findByTipo(
                tipo.trim().toUpperCase(Locale.ROOT)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        validarId(id);

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoría no encontrada con ID: " + id
                        )
                );
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        validarCategoria(categoria);

        categoria.setNombre(categoria.getNombre().trim());
        categoria.setTipo(
                normalizarTipo(categoria.getTipo())
        );

        return categoriaRepository.save(categoria);
    }

    @Override
    public Categoria actualizar(
            Long id,
            Categoria categoriaActualizada) {

        validarId(id);
        validarCategoria(categoriaActualizada);

        Categoria existente = buscarPorId(id);

        existente.setNombre(
                categoriaActualizada.getNombre().trim()
        );
        existente.setTipo(
                normalizarTipo(categoriaActualizada.getTipo())
        );

        return categoriaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoriaRepository.delete(categoria);
    }

    private void validarCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new ReglaNegocioException(
                    "Los datos de la categoría son obligatorios"
            );
        }

        validarNombre(categoria.getNombre());
        validarTipo(categoria.getTipo());
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ReglaNegocioException(
                    "El nombre de la categoría es obligatorio"
            );
        }
    }

    private void validarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new ReglaNegocioException(
                    "El tipo de categoría es obligatorio (GASTO o INGRESO)"
            );
        }

        String tipoNormalizado = normalizarTipo(tipo);

        if (!tipoNormalizado.equals("GASTO")
                && !tipoNormalizado.equals("INGRESO")) {
            throw new ReglaNegocioException(
                    "El tipo de categoría debe ser GASTO o INGRESO"
            );
        }
    }

    private String normalizarTipo(String tipo) {
        return tipo.trim().toUpperCase(Locale.ROOT);
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID de la categoría debe ser válido"
            );
        }
    }
}
