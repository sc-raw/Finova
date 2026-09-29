package com.finova.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroRequestDTO {
    private String nombres;
    private String apellidos;
    private String correo;
    private String password;
}