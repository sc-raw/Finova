package com.finova.controller;

import com.finova.entity.Comprobante;
import com.finova.service.ComprobanteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @GetMapping
    public ResponseEntity<List<Comprobante>> listarComprobantes() {
        return ResponseEntity.ok(comprobanteService.listarComprobantes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprobante> buscarPorId(@PathVariable Long id) {
        return comprobanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Comprobante> guardar(@RequestBody Comprobante comprobante) {
        return ResponseEntity.ok(comprobanteService.guardar(comprobante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        comprobanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}