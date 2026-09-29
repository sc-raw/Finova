package com.finova.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CambiarPasswordRequestDTO {
    private String passwordActual;
    private String passwordNueva;
}