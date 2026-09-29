package com.finova.controller;

import com.finova.entity.Banco;
import com.finova.service.BancoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService bancoService;

    @GetMapping
    public ResponseEntity<List<Banco>> listarBancos() {
        return ResponseEntity.ok(bancoService.listarBancos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Banco>> listarActivos() {
        return ResponseEntity.ok(bancoService.listarActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Banco> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(bancoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Banco> guardar(@RequestBody Banco banco) {
        return ResponseEntity.ok(bancoService.guardar(banco));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Banco> actualizar(@PathVariable Long id, @RequestBody Banco banco) {
        return ResponseEntity.ok(bancoService.actualizar(id, banco));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bancoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}