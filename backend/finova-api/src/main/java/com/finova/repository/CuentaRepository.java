package com.finova.repository;

import com.finova.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    // Listar cuentas de un usuario
    List<Cuenta> findByUsuarioId(Long usuarioId);

   

    // SP: Listar cuentas por usuario
    @Procedure(name = "sp_listar_cuentas")
    List<Object[]> listarCuentasPorUsuario(@Param("p_user_id") Integer userId);

    // SP: Crear cuenta
    @Procedure(name = "sp_crear_cuenta")
    void crearCuenta(
        @Param("p_user_id") Integer userId,
        @Param("p_name") String name,
        @Param("p_currency") String currency,
        @Param("p_type") String type,
        @Param("p_bank_name") String bankName,
        @Param("p_last_four") String lastFour,
        @Param("p_initial_balance") Double initialBalance
    );
}