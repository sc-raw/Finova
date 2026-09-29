package com.finova.repository;

import com.finova.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    // Listar metas de un usuario
    List<Meta> findByUsuarioId(Long usuarioId);

    // ==================== SP ====================

    // SP: Listar metas con progreso
    @Procedure(name = "sp_listar_metas")
    List<Object[]> listarMetasPorUsuario(@Param("p_user_id") Integer userId);

    // SP: Crear meta
    @Procedure(name = "sp_crear_meta")
    void crearMeta(
        @Param("p_user_id") Integer userId,
        @Param("p_name") String name,
        @Param("p_icon") String icon,
        @Param("p_target_amount") BigDecimal targetAmount,
        @Param("p_deadline_date") LocalDate deadlineDate,
        @Param("p_scheduled_contribution") BigDecimal scheduledContribution
    );

    // SP: Aportar a meta
    @Procedure(name = "sp_aportar_meta")
    void aportarMeta(
        @Param("p_user_id") Integer userId,
        @Param("p_cuenta_id") Integer cuentaId,
        @Param("p_meta_id") Integer metaId,
        @Param("p_monto") BigDecimal monto,
        @Param("p_descripcion") String descripcion
    );

    // SP: Obtener detalle de meta
    @Procedure(name = "sp_obtener_meta")
    List<Object[]> obtenerMeta(@Param("p_goal_id") Integer goalId);
}