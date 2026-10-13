package com.finova.service.imp;

import com.finova.entity.Usuario;
import com.finova.exception.CredencialesInvalidasException;
import com.finova.exception.RecursoNoEncontradoException;
import com.finova.exception.ReglaNegocioException;
import com.finova.repository.UsuarioRepository;
import com.finova.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImp implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new ReglaNegocioException(
                    "El ID del usuario debe ser válido"
            );
        }

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario no encontrado con ID: " + id
                        )
                );
    }

    @Override
    @Transactional
    public Usuario registrar(Usuario usuario) {
        validarDatosUsuario(usuario);

        String correo = usuario.getCorreo().trim().toLowerCase();

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ReglaNegocioException(
                    "El correo ya está registrado"
            );
        }

        usuario.setCorreo(correo);
        usuario.setNombres(usuario.getNombres().trim());
        usuario.setApellidos(usuario.getApellidos().trim());
        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        validarDatosUsuario(usuario);

        usuario.setPassword(
                passwordEncoder.encode(usuario.getPassword())
        );

        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario actualizar(Long id, Usuario usuarioActualizado) {
        if (usuarioActualizado == null) {
            throw new ReglaNegocioException(
                    "Los datos del usuario son obligatorios"
            );
        }

        Usuario existente = buscarPorId(id);

        if (usuarioActualizado.getNombres() == null
                || usuarioActualizado.getNombres().isBlank()
                || usuarioActualizado.getApellidos() == null
                || usuarioActualizado.getApellidos().isBlank()) {
            throw new ReglaNegocioException(
                    "Los nombres y apellidos son obligatorios"
            );
        }

        String correo = usuarioActualizado.getCorreo();

        if (correo == null || correo.isBlank()
                || !correo.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ReglaNegocioException(
                    "El correo electrónico no es válido"
            );
        }

        correo = correo.trim().toLowerCase();

        if (!existente.getCorreo().equalsIgnoreCase(correo)) {
            if (usuarioRepository.existsByCorreo(correo)) {
                throw new ReglaNegocioException(
                        "El correo ya está en uso por otro usuario"
                );
            }

            existente.setCorreo(correo);
        }

        existente.setNombres(usuarioActualizado.getNombres().trim());
        existente.setApellidos(usuarioActualizado.getApellidos().trim());
        existente.setFoto(usuarioActualizado.getFoto());

        return usuarioRepository.save(existente);
    }

    @Override
    @Transactional
    public void cambiarPassword(
            Long id,
            String passwordActual,
            String passwordNueva) {

        Usuario usuario = buscarPorId(id);

        if (passwordActual == null || passwordActual.isBlank()
                || !passwordEncoder.matches(
                        passwordActual, usuario.getPassword())) {
            throw new CredencialesInvalidasException(
                    "La contraseña actual es incorrecta"
            );
        }

        if (passwordNueva == null || passwordNueva.length() < 8) {
            throw new ReglaNegocioException(
                    "La nueva contraseña debe tener al menos 8 caracteres"
            );
        }

        if (passwordEncoder.matches(
                passwordNueva, usuario.getPassword())) {
            throw new ReglaNegocioException(
                    "La nueva contraseña debe ser diferente de la actual"
            );
        }

        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuarioRepository.delete(usuario);
    }

    @Override
    public Usuario login(String correo, String password) {
        if (correo == null || correo.isBlank()
                || password == null || password.isBlank()) {
            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos"
            );
        }

        Usuario usuario = usuarioRepository
                .findByCorreo(correo.trim().toLowerCase())
                .orElseThrow(() ->
                        new CredencialesInvalidasException(
                                "Correo o contraseña incorrectos"
                        )
                );

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new CredencialesInvalidasException(
                    "Correo o contraseña incorrectos"
            );
        }

        return usuario;
    }

    private void validarDatosUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new ReglaNegocioException(
                    "Los datos del usuario son obligatorios"
            );
        }

        if (usuario.getNombres() == null
                || usuario.getNombres().isBlank()) {
            throw new ReglaNegocioException(
                    "Los nombres son obligatorios"
            );
        }

        if (usuario.getApellidos() == null
                || usuario.getApellidos().isBlank()) {
            throw new ReglaNegocioException(
                    "Los apellidos son obligatorios"
            );
        }

        if (usuario.getCorreo() == null
                || usuario.getCorreo().isBlank()
                || !usuario.getCorreo().trim()
                        .matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ReglaNegocioException(
                    "El correo electrónico no es válido"
            );
        }

        if (usuario.getPassword() == null
                || usuario.getPassword().length() < 8) {
            throw new ReglaNegocioException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }
    }
}