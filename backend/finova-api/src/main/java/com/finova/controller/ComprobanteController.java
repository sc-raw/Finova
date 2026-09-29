package com.finova.controller;

import com.finova.entity.Comprobante;
import com.finova.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.finova.dto.InterpretarComprobanteRequestDTO;
import com.finova.dto.InterpretarComprobanteResponseDTO;
import com.finova.service.GeminiService;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {
	// Inyectar el servicio
	private final GeminiService geminiService;

    private final ComprobanteService comprobanteService;

    @GetMapping
    public ResponseEntity<List<Comprobante>> listarComprobantes() {
        return ResponseEntity.ok(comprobanteService.listarComprobantes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprobante> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(comprobanteService.buscarPorId(id));
    }

    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<Comprobante> buscarPorMovimiento(@PathVariable Long movimientoId) {
        return ResponseEntity.ok(comprobanteService.buscarPorMovimiento(movimientoId));
    }

    @PostMapping
    public ResponseEntity<Comprobante> guardar(@RequestBody Comprobante comprobante) {
        return ResponseEntity.ok(comprobanteService.guardar(comprobante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comprobante> actualizar(@PathVariable Long id, @RequestBody Comprobante comprobante) {
        return ResponseEntity.ok(comprobanteService.actualizar(id, comprobante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comprobanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    
 // Endpoint nuevo
    @PostMapping("/interpretar")
    public ResponseEntity<InterpretarComprobanteResponseDTO> interpretar(@RequestBody InterpretarComprobanteRequestDTO request) {
        return ResponseEntity.ok(geminiService.interpretarComprobante(request.getOcrText()));
    }
}