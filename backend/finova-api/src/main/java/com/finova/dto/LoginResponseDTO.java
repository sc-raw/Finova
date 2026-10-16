package com.finova.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipo;
    private UsuarioResponseDTO usuario;
}