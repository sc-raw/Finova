package com.finova.service;

import com.finova.entity.Usuario;
import com.finova.exception.CredencialesInvalidasException;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ==================== CRUD ====================

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    @Transactional
    public Usuario registrar(Usuario usuario) {
        // Validar que el correo no exista
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new ReglaNegocioException("El correo ya está registrado: " + usuario.getCorreo());
        }
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        Usuario existente = buscarPorId(id);
        existente.setNombres(usuarioActualizado.getNombres());
        existente.setApellidos(usuarioActualizado.getApellidos());
        existente.setFoto(usuarioActualizado.getFoto());
        // El correo solo se actualiza si cambió y no existe en otro usuario
        if (!existente.getCorreo().equals(usuarioActualizado.getCorreo())) {
            if (usuarioRepository.existsByCorreo(usuarioActualizado.getCorreo())) {
                throw new ReglaNegocioException("El correo ya está en uso por otro usuario");
            }
            existente.setCorreo(usuarioActualizado.getCorreo());
        }
        return usuarioRepository.save(existente);
    }

    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        Usuario usuario = buscarPorId(id);
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new CredencialesInvalidasException("La contraseña actual es incorrecta");
        }
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    // ==================== LOGIN ====================

    public Usuario login(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new CredencialesInvalidasException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos");
        }
        return usuario;
    }
}