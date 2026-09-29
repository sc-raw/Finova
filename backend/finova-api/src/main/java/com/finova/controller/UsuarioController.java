package com.finova.controller;

import com.finova.dto.CambiarPasswordRequestDTO;
import com.finova.dto.LoginRequestDTO;
import com.finova.dto.RegistroRequestDTO;
import com.finova.dto.UsuarioResponseDTO;
import com.finova.entity.Usuario;
import com.finova.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarUsuarios() {
        List<UsuarioResponseDTO> lista = usuarioService.listarUsuarios()
                .stream()
                .map(UsuarioResponseDTO::desdeEntidad)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponseDTO.desdeEntidad(usuarioService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody RegistroRequestDTO request) {
        Usuario nuevo = new Usuario();
        nuevo.setNombres(request.getNombres());
        nuevo.setApellidos(request.getApellidos());
        nuevo.setCorreo(request.getCorreo());
        nuevo.setPassword(request.getPassword());

        Usuario guardado = usuarioService.registrar(nuevo);
        return ResponseEntity.ok(UsuarioResponseDTO.desdeEntidad(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(UsuarioResponseDTO.desdeEntidad(usuarioService.actualizar(id, usuario)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponseDTO> login(@RequestBody LoginRequestDTO request) {
        Usuario usuario = usuarioService.login(request.getCorreo(), request.getPassword());
        return ResponseEntity.ok(UsuarioResponseDTO.desdeEntidad(usuario));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long id, @RequestBody CambiarPasswordRequestDTO request) {
        usuarioService.cambiarPassword(id, request.getPasswordActual(), request.getPasswordNueva());
        return ResponseEntity.noContent().build();
    }
}