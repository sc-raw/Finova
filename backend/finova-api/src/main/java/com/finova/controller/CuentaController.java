
package com.finova.controller;

import com.finova.dto.CuentaResponseDTO;
import com.finova.entity.Cuenta;
import com.finova.service.CuentaService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> listarCuentas() {
        List<CuentaResponseDTO> lista = cuentaService.listarCuentas()
                .stream()
                .map(CuentaResponseDTO::desdeEntidad)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CuentaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        List<CuentaResponseDTO> lista = cuentaService
                .listarPorUsuario(usuarioId)
                .stream()
                .map(CuentaResponseDTO::desdeEntidad)
                .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                CuentaResponseDTO.desdeEntidad(
                        cuentaService.buscarPorId(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<CuentaResponseDTO> guardar(
            @RequestBody Cuenta cuenta) {

        Cuenta guardada = cuentaService.guardar(cuenta);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CuentaResponseDTO.desdeEntidad(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> actualizar(
            @PathVariable Long id,
            @RequestBody Cuenta cuenta) {

        Cuenta actualizada = cuentaService.actualizar(id, cuenta);

        return ResponseEntity.ok(
                CuentaResponseDTO.desdeEntidad(actualizada)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {

        cuentaService.eliminar(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/sumar")
    public ResponseEntity<Void> sumarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        if (body == null || body.get("monto") == null) {
            throw new IllegalArgumentException(
                    "El campo 'monto' es obligatorio"
            );
        }

        cuentaService.sumarSaldo(id, body.get("monto"));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restar")
    public ResponseEntity<Void> restarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        if (body == null || body.get("monto") == null) {
            throw new IllegalArgumentException(
                    "El campo 'monto' es obligatorio"
            );
        }

        cuentaService.restarSaldo(id, body.get("monto"));

        return ResponseEntity.noContent().build();
    }
}
