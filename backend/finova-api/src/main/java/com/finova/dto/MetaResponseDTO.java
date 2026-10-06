package com.finova.dto;

import com.finova.entity.Meta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MetaResponseDTO {

    private Long id;

    private Long usuarioId;

    private String nombre;

    private BigDecimal montoObjetivo;
    private BigDecimal montoActual;

    private LocalDate fechaObjetivo;

    public static MetaResponseDTO desdeEntidad(Meta meta) {

        return new MetaResponseDTO(
                meta.getId(),
                meta.getUsuario() != null
                        ? meta.getUsuario().getId()
                        : null,
                meta.getNombre(),
                meta.getMontoObjetivo(),
                meta.getMontoActual(),
                meta.getFechaObjetivo()
        );
    }
}