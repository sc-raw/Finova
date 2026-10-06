package com.finova.controller;

import com.finova.dto.CuentaResponseDTO;
import com.finova.entity.Cuenta;
import com.finova.service.CuentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CuentaResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId) {

        List<CuentaResponseDTO> lista = cuentaService.listarPorUsuario(usuarioId)
                .stream()
                .map(CuentaResponseDTO::desdeEntidad)
                .collect(Collectors.toList());

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
    public ResponseEntity<Cuenta> guardar(
            @RequestBody Cuenta cuenta) {

        return ResponseEntity.ok(
                cuentaService.guardar(cuenta)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> actualizar(
            @PathVariable Long id,
            @RequestBody Cuenta cuenta) {

        return ResponseEntity.ok(
                cuentaService.actualizar(id, cuenta)
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

        cuentaService.sumarSaldo(id, body.get("monto"));

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/restar")
    public ResponseEntity<Void> restarSaldo(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body) {

        cuentaService.restarSaldo(id, body.get("monto"));

        return ResponseEntity.noContent().build();
    }
}