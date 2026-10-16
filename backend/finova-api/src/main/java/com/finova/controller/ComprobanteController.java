package com.finova.controller;

import com.finova.dto.InterpretarComprobanteRequestDTO;
import com.finova.dto.InterpretarComprobanteResponseDTO;
import com.finova.entity.Comprobante;
import com.finova.service.ComprobanteService;
import com.finova.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.finova.service.OcrService;
import com.finova.service.AlmacenamientoComprobanteService;

import com.finova.dto.ConfirmarComprobanteRequestDTO;
import com.finova.entity.Movimiento;
import com.finova.service.MovimientoService;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
@RequiredArgsConstructor
public class ComprobanteController {

    private final GeminiService geminiService;
    private final ComprobanteService comprobanteService;
    private final OcrService ocrService;
    private final AlmacenamientoComprobanteService almacenamientoComprobanteService;
    private final MovimientoService movimientoService;

    @GetMapping
    public ResponseEntity<List<Comprobante>> listarComprobantes() {
        return ResponseEntity.ok(
                comprobanteService.listarComprobantes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comprobante> buscarPorId(
            @PathVariable Long id) {
        return ResponseEntity.ok(
                comprobanteService.buscarPorId(id)
        );
    }

    @GetMapping("/movimiento/{movimientoId}")
    public ResponseEntity<Comprobante> buscarPorMovimiento(
            @PathVariable Long movimientoId) {
        return ResponseEntity.ok(
                comprobanteService.buscarPorMovimiento(movimientoId)
        );
    }

    @PostMapping
    public ResponseEntity<Comprobante> guardar(
            @RequestBody(required = false) Comprobante comprobante) {

        if (comprobante == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(comprobanteService.guardar(comprobante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comprobante> actualizar(
            @PathVariable Long id,
            @RequestBody Comprobante comprobante) {
        return ResponseEntity.ok(
                comprobanteService.actualizar(id, comprobante)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id) {
        comprobanteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/interpretar")
    public ResponseEntity<InterpretarComprobanteResponseDTO> interpretar(
            @RequestBody InterpretarComprobanteRequestDTO request) {
        return ResponseEntity.ok(
                geminiService.interpretarComprobante(request.getOcrText())
        );
    }


    @PostMapping("/confirmar")
    public ResponseEntity<Movimiento> confirmarComprobante(
            @RequestBody ConfirmarComprobanteRequestDTO request) {

        Movimiento movimiento =
                movimientoService.confirmarComprobante(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(movimiento);
    }


    @PostMapping(value = "/analizar-imagen", consumes = "multipart/form-data")
    public ResponseEntity<InterpretarComprobanteResponseDTO> analizarImagen(
            @RequestParam("imagen") MultipartFile imagen) throws IOException {

        String rutaImagen = almacenamientoComprobanteService.guardarImagen(imagen);

        try {
            String textoOCR = ocrService.extraerTexto(rutaImagen);

            InterpretarComprobanteResponseDTO resultado =
                    geminiService.interpretarComprobante(textoOCR);

            resultado.setRutaImagen(rutaImagen);

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            almacenamientoComprobanteService.eliminarImagen(rutaImagen);
            throw e;
        }
    }





    @GetMapping("/{id}/imagen")
    public ResponseEntity<Resource> verImagen(@PathVariable Long id)
            throws IOException {

        Comprobante comprobante = comprobanteService.buscarPorId(id);

        String rutaImagen = comprobante.getRutaComprobante();

        if (rutaImagen == null || rutaImagen.isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path ruta = Paths.get(rutaImagen).toAbsolutePath().normalize();

        if (!Files.exists(ruta) || !Files.isRegularFile(ruta)) {
            return ResponseEntity.notFound().build();
        }

        Resource imagen = new UrlResource(ruta.toUri());

        String tipoContenido = Files.probeContentType(ruta);

        MediaType mediaType = tipoContenido != null
                ? MediaType.parseMediaType(tipoContenido)
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(imagen);
    }









}
