package com.finova.controller;

import com.finova.dto.CambiarPasswordRequestDTO;
import com.finova.dto.LoginRequestDTO;
import com.finova.dto.RegistroRequestDTO;
import com.finova.dto.UsuarioResponseDTO;
import com.finova.entity.Usuario;
import com.finova.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finova.dto.LoginResponseDTO;
import com.finova.security.JwtService;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(
                usuarioService.listarUsuarios()
                        .stream()
                        .map(UsuarioResponseDTO::desdeEntidad)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                UsuarioResponseDTO.desdeEntidad(
                        usuarioService.buscarPorId(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @RequestBody RegistroRequestDTO request) {

        Usuario nuevo = new Usuario();
        nuevo.setNombres(request.getNombres());
        nuevo.setApellidos(request.getApellidos());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setPassword(request.getPassword());

        Usuario guardado = usuarioService.registrar(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponseDTO.desdeEntidad(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Usuario usuario) {
        return ResponseEntity.ok(
                UsuarioResponseDTO.desdeEntidad(
                        usuarioService.actualizar(id, usuario)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        // 1. Validar credenciales contra la base de datos
        Usuario usuario = usuarioService.login(request.getCorreo(), request.getPassword());

        // 2. Generar el token JWT pasando el ID (Long) y el Correo (String)
        String token = jwtService.generateToken(usuario.getId(), usuario.getCorreo());

        // 3. Crear la respuesta con los datos limpios estructurados
        LoginResponseDTO respuesta = new LoginResponseDTO(
                usuario.getId(),
                usuario.getNombres(),
                usuario.getCorreo(),
                token
        );

        return ResponseEntity.ok(respuesta);
    }


    @PutMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(
            @PathVariable Long id,
            @RequestBody CambiarPasswordRequestDTO request) {
        usuarioService.cambiarPassword(
                id,
                request.getPasswordActual(),
                request.getPasswordNueva()
        );

        return ResponseEntity.noContent().build();
    }
}
