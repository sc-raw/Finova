package com.finova.controller;

import com.finova.dto.EstadisticasResponseDTO;
import com.finova.service.EstadisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<EstadisticasResponseDTO> obtenerEstadisticas(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "mes") String periodo
    ) {
        return ResponseEntity.ok(estadisticaService.calcular(usuarioId, periodo));
    }
}