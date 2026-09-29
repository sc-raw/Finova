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

    @GetMapping("/{id}")
    public ResponseEntity<Banco> buscarPorId(@PathVariable Long id) {
        return bancoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Banco> guardar(@RequestBody Banco banco) {
        return ResponseEntity.ok(bancoService.guardar(banco));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        bancoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}