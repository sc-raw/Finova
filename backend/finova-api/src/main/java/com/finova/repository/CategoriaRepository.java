package com.finova.repository;

import com.finova.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Listar categorías por tipo (GASTO / INGRESO)
    List<Categoria> findByTipo(String tipo);

    // ==================== SP ====================

    // SP: Listar categorías
    @Procedure(name = "sp_listar_categorias")
    List<Object[]> listarCategoriasPorTipo(@Param("p_tipo") String tipo);

    // SP: Crear categoría
    @Procedure(name = "sp_crear_categoria")
    void crearCategoria(
        @Param("p_name") String name,
        @Param("p_icon") String icon,
        @Param("p_color") String color,
        @Param("p_type") String type
    );

    // SP: Buscar comercio (autocompletar)
    @Procedure(name = "sp_buscar_comercio")
    List<Object[]> buscarComercio(
        @Param("p_user_id") Integer userId,
        @Param("p_keyword") String keyword
    );
}