package com.finova.repository;

import com.finova.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por correo (para login)
    Optional<Usuario> findByCorreo(String correo);

    // Verificar si existe un correo
    boolean existsByCorreo(String correo);

    // Llamar a la SP de login
    @Procedure(name = "sp_login")
    List<Object[]> login(@Param("p_email") String email);
}