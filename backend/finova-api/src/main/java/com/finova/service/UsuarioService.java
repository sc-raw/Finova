package com.finova.service;

import com.finova.entity.Usuario;

import java.util.List;

public interface UsuarioService {

    List<Usuario> listarUsuarios();

    Usuario buscarPorId(Long id);

    Usuario registrar(Usuario usuario);

    Usuario guardar(Usuario usuario);

    Usuario actualizar(Long id, Usuario usuarioActualizado);

    void cambiarPassword(
            Long id,
            String passwordActual,
            String passwordNueva
    );

    void eliminar(Long id);

    Usuario login(String correo, String password);
}