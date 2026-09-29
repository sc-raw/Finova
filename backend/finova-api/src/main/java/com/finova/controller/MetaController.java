package com.finova.controller;

import com.finova.entity.Meta;
import com.finova.service.MetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @GetMapping
    public ResponseEntity<List<Meta>> listarMetas() {
        return ResponseEntity.ok(metaService.listarMetas());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Meta>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(metaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.buscarPorId(id));
    }

    @GetMapping("/{id}/progreso")
    public ResponseEntity<BigDecimal> calcularProgreso(@PathVariable Long id) {
        return ResponseEntity.ok(metaService.calcularProgreso(id));
    }

    @PostMapping
    public ResponseEntity<Meta> guardar(@RequestBody Meta meta) {
        return ResponseEntity.ok(metaService.guardar(meta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meta> actualizar(@PathVariable Long id, @RequestBody Meta meta) {
        return ResponseEntity.ok(metaService.actualizar(id, meta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== OPERACIONES ====================

    @PutMapping("/{id}/aportar")
    public ResponseEntity<Void> aportar(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        metaService.aportar(id, body.get("monto"));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/retirar")
    public ResponseEntity<Void> retirar(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        metaService.retirar(id, body.get("monto"));
        return ResponseEntity.noContent().build();
    }
}