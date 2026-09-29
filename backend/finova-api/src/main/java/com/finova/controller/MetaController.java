package com.finova.controller;

import com.finova.entity.Meta;
import com.finova.service.MetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/metas")
@RequiredArgsConstructor
public class MetaController {

    private final MetaService metaService;

    @GetMapping
    public ResponseEntity<List<Meta>> listarMetas() {
        return ResponseEntity.ok(metaService.listarMetas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meta> buscarPorId(@PathVariable Long id) {
        return metaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Meta> guardar(@RequestBody Meta meta) {
        return ResponseEntity.ok(metaService.guardar(meta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        metaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}