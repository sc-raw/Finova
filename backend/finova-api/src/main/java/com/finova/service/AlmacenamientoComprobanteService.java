package com.finova.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlmacenamientoComprobanteService {

    @Value("${finova.comprobantes.ruta}")
    private String rutaComprobantes;

    public String guardarImagen(MultipartFile imagen) throws IOException {

        if (imagen == null || imagen.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una imagen"
            );
        }

        String nombreOriginal = imagen.getOriginalFilename();
        String extension = "";

        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(
                    nombreOriginal.lastIndexOf(".")
            ).toLowerCase();
        }

        if (!extension.matches("\\.(jpg|jpeg|png|webp)")) {
            throw new IllegalArgumentException(
                    "Formato no permitido. Use JPG, JPEG, PNG o WEBP"
            );
        }

        Path directorio = Paths.get(rutaComprobantes)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directorio);

        String nombreArchivo = UUID.randomUUID() + extension;
        Path destino = directorio.resolve(nombreArchivo).normalize();

        if (!destino.getParent().equals(directorio)) {
            throw new IllegalArgumentException(
                    "Ruta de archivo no válida"
            );
        }

        imagen.transferTo(destino);

        return destino.toString();
    }
}