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

@Service
@RequiredArgsConstructor
public class CategoriaServiceImp implements CategoriaService {

    private final CategoriaRepository categoriaRepository;


    // =========================================================
    // LISTAR CATEGORÍAS
    // =========================================================

    @Override
    public List<Categoria> listarCategorias() {

        return categoriaRepository.findAll();
    }


    // =========================================================
    // LISTAR POR TIPO
    // =========================================================

    @Override
    public List<Categoria> listarPorTipo(String tipo) {

        validarTipo(tipo);

        return categoriaRepository.findByTipo(
                tipo.trim().toUpperCase()
        );
    }


    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Override
    public Categoria buscarPorId(Long id) {

        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Categoría no encontrada con ID: " + id
                        )
                );
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    @Override
    @Transactional
    public Categoria guardar(Categoria categoria) {

        validarNombre(categoria.getNombre());
        validarTipo(categoria.getTipo());

        categoria.setNombre(
                categoria.getNombre().trim()
        );

        categoria.setTipo(
                categoria.getTipo().trim().toUpperCase()
        );

        return categoriaRepository.save(categoria);
    }


    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Override
    @Transactional
    public Categoria actualizar(
            Long id,
            Categoria categoriaActualizada) {

        Categoria existente = buscarPorId(id);

        validarNombre(
                categoriaActualizada.getNombre()
        );

        validarTipo(
                categoriaActualizada.getTipo()
        );

        existente.setNombre(
                categoriaActualizada.getNombre().trim()
        );

        existente.setTipo(
                categoriaActualizada.getTipo()
                        .trim()
                        .toUpperCase()
        );

        return categoriaRepository.save(existente);
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    @Override
    @Transactional
    public void eliminar(Long id) {

        if (!categoriaRepository.existsById(id)) {

            throw new RecursoNoEncontradoException(
                    "Categoría no encontrada con ID: " + id
            );
        }

        categoriaRepository.deleteById(id);
    }


    // =========================================================
    // VALIDAR NOMBRE
    // =========================================================

    private void validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {

            throw new ReglaNegocioException(
                    "El nombre de la categoría es obligatorio"
            );
        }
    }


    // =========================================================
    // VALIDAR TIPO
    // =========================================================

    private void validarTipo(String tipo) {

        if (tipo == null || tipo.isBlank()) {

            throw new ReglaNegocioException(
                    "El tipo de categoría es obligatorio "
                            + "(GASTO o INGRESO)"
            );
        }

        String tipoNormalizado =
                tipo.trim().toUpperCase();

        if (!tipoNormalizado.equals("GASTO")
                && !tipoNormalizado.equals("INGRESO")) {

            throw new ReglaNegocioException(
                    "El tipo de categoría debe ser "
                            + "GASTO o INGRESO"
            );
        }
    }
}