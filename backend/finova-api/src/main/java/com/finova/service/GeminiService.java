
package com.finova.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.dto.InterpretarComprobanteResponseDTO;
import com.finova.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${ollama.api.url}")
    private String ollamaApiUrl;

    @Value("${ollama.api.model:qwen2.5:3b}")
    private String ollamaModel;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().build();

    public InterpretarComprobanteResponseDTO interpretarComprobante(
            String ocrText) {

        if (ocrText == null || ocrText.isBlank()) {
            throw new ReglaNegocioException(
                    "El texto OCR del comprobante es obligatorio"
            );
        }

        if (ollamaApiUrl == null || ollamaApiUrl.isBlank()) {
            throw new ReglaNegocioException(
                    "La configuración de Ollama está incompleta"
            );
        }

        String prompt = construirPrompt(ocrText);

        Map<String, Object> body = Map.of(
                "model", ollamaModel,
                "prompt", prompt,
                "stream", false,
                "format", "json",
                "options", Map.of(
                        "temperature", 0.2
                )
        );

        try {
            String response = webClient.post()
                    .uri(ollamaApiUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                throw new ReglaNegocioException(
                        "Ollama devolvió una respuesta vacía"
                );
            }

            JsonNode root = objectMapper.readTree(response);
            JsonNode responseNode = root.path("response");

            if (!responseNode.isTextual()
                    || responseNode.asText().isBlank()) {
                throw new ReglaNegocioException(
                        "Ollama no devolvió texto válido"
                );
            }

            return parsearRespuesta(responseNode.asText());

        } catch (WebClientResponseException e) {
            throw new ReglaNegocioException(
                    "Error de Ollama: HTTP "
                            + e.getStatusCode().value()
            );
        } catch (ReglaNegocioException e) {
            throw e;
        } catch (Exception e) {
            throw new ReglaNegocioException(
                    "No se pudo interpretar el comprobante con Ollama"
            );
        }
    }

    private String construirPrompt(String ocrText) {
        return """
                Eres un asistente financiero que analiza comprobantes peruanos.

                Devuelve exclusivamente un JSON válido con esta estructura:

                {
                  "razonamiento": "explicación breve",
                  "tipoDocumento": "boleta|factura|recibo|voucher|otro",
                  "comercio": "nombre del comercio",
                  "ruc": null,
                  "fecha": null,
                  "moneda": "PEN",
                  "subtotal": null,
                  "igv": null,
                  "total": null,
                  "categoriaSugerida": "Alimentación|Servicios|Transporte|Salud|Ocio|Otros",
                  "confianza": 0.0,
                  "items": [
                    {
                      "nombre": "producto",
                      "cantidad": 1,
                      "precio": 0.0
                    }
                  ]
                }

                Reglas:
                - No inventes información que no aparezca en el comprobante.
                - Si un dato no está disponible, utiliza null.
                - Los importes deben ser números, no cadenas de texto.
                - La confianza debe ser un número entre 0 y 1.
                - La fecha debe estar en formato ISO 8601 (AAAA-MM-DD) o ser null.
                - Usa PEN para soles peruanos y USD para dólares.
                - Si no hay productos identificables, devuelve items como [].
                - Devuelve únicamente el JSON, sin bloques Markdown.

                Texto OCR del comprobante:
                """ + ocrText;
    }

    private InterpretarComprobanteResponseDTO parsearRespuesta(
            String textoJson) throws Exception {

        JsonNode data = objectMapper.readTree(textoJson);

        InterpretarComprobanteResponseDTO dto =
                new InterpretarComprobanteResponseDTO();

        dto.setRazonamiento(data.path("razonamiento").asText(""));
        dto.setTipoDocumento(data.path("tipoDocumento").asText(""));
        dto.setComercio(data.path("comercio").asText(""));
        dto.setRuc(textoOpcional(data, "ruc"));
        dto.setFecha(textoOpcional(data, "fecha"));
        dto.setMoneda(data.path("moneda").asText("PEN"));
        dto.setSubtotal(decimalOpcional(data, "subtotal"));
        dto.setIgv(decimalOpcional(data, "igv"));
        dto.setTotal(decimalOpcional(data, "total"));
        dto.setCategoriaSugerida(
                data.path("categoriaSugerida").asText("Otros")
        );
        dto.setConfianza(decimalOpcional(data, "confianza"));

        List<InterpretarComprobanteResponseDTO.ItemDTO> items =
                new ArrayList<>();

        JsonNode itemsNode = data.path("items");

        if (itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                items.add(
                        new InterpretarComprobanteResponseDTO.ItemDTO(
                                item.path("nombre").asText(""),
                                item.path("cantidad").asInt(1),
                                decimalOpcional(item, "precio")
                        )
                );
            }
        }

        dto.setItems(items);
        return dto;
    }

    private String textoOpcional(JsonNode data, String campo) {
        JsonNode valor = data.get(campo);

        if (valor == null || valor.isNull()
                || valor.asText().isBlank()) {
            return null;
        }

        return valor.asText();
    }

    private BigDecimal decimalOpcional(JsonNode data, String campo) {
        JsonNode valor = data.get(campo);

        if (valor == null || valor.isNull()
                || valor.asText().isBlank()
                || (!valor.isNumber() && !valor.isTextual())) {
            return null;
        }

        try {
            return new BigDecimal(valor.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
