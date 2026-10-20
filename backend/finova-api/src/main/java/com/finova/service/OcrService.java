
package com.finova.service;

import java.io.File;

import org.springframework.stereotype.Service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Service
public class OcrService {

    private static final String TESSERACT_DATA_PATH =
            "C:\\Program Files\\Tesseract-OCR\\tessdata";

    @SuppressWarnings("unused")
    private static final String TESSERACT_EXECUTABLE =
            "C:\\Program Files\\Tesseract-OCR\\tesseract.exe";

    public String extraerTexto(String rutaImagen) {
        ITesseract tesseract = new Tesseract();

        tesseract.setDatapath(TESSERACT_DATA_PATH);
        tesseract.setLanguage("spa");

        try {
            File imagen = new File(rutaImagen);

            if (!imagen.exists() || !imagen.isFile()) {
                throw new IllegalArgumentException(
                        "No se encontró la imagen: " + rutaImagen);
            }

            BufferedImage imagenLeida = ImageIO.read(imagen);

            if (imagenLeida == null) {
                throw new IllegalArgumentException(
                        "El archivo no es una imagen válida o su formato no es compatible.");
            }

            return tesseract.doOCR(imagenLeida);

        } catch (TesseractException e) {
            throw new RuntimeException(
                    "No se pudo extraer texto de la imagen", e);
        } catch (java.io.IOException e) {
            throw new RuntimeException(
                    "No se pudo leer el archivo de imagen", e);
        }
    }
}
