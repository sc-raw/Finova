package com.finova.dto;

import com.finova.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String nombres;
    private String apellidos;
    private String correo;
    private String foto;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponseDTO desdeEntidad(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNombres(),
            usuario.getApellidos(),
            usuario.getCorreo(),
            usuario.getFoto(),
            usuario.getFechaCreacion()
        );
    }
}