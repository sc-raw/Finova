package com.finova.dto;

import com.finova.entity.Cuenta;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponseDTO {

    private Long id;

    private Long usuarioId;

    private Long bancoId;
    private String bancoNombre;

    private String numeroCuenta;
    private String moneda;
    private String nombre;

    private BigDecimal saldoInicial;
    private BigDecimal saldoActual;

    public static CuentaResponseDTO desdeEntidad(Cuenta cuenta) {

        return new CuentaResponseDTO(
                cuenta.getId(),
                cuenta.getUsuario() != null
                        ? cuenta.getUsuario().getId()
                        : null,
                cuenta.getBanco() != null
                        ? cuenta.getBanco().getId()
                        : null,
                cuenta.getBanco() != null
                        ? cuenta.getBanco().getNombre()
                        : null,
                cuenta.getNumeroCuenta(),
                cuenta.getMoneda() != null
                        ? cuenta.getMoneda().name()
                        : null,
                cuenta.getNombre(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldoActual()
        );
    }
}