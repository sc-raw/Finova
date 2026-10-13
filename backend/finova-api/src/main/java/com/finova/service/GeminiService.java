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

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder().build();

    public InterpretarComprobanteResponseDTO interpretarComprobante(String ocrText) {
        if (ocrText == null || ocrText.isBlank()) {
            throw new ReglaNegocioException(
                    "El texto OCR del comprobante es obligatorio"
            );
        }

        if (apiKey == null || apiKey.isBlank()
                || apiUrl == null || apiUrl.isBlank()) {
            throw new ReglaNegocioException(
                    "La configuración de Gemini está incompleta"
            );
        }

        String prompt = construirPrompt(ocrText);

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                ),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "responseMimeType", "application/json"
                )
        );

        try {
            String response = webClient.post()
                    .uri(apiUrl + (apiUrl.contains("?") ? "&" : "?")
                            + "key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null || response.isBlank()) {
                throw new ReglaNegocioException(
                        "Gemini devolvió una respuesta vacía"
                );
            }

            return parsearRespuesta(response);

        } catch (WebClientResponseException e) {
            throw new ReglaNegocioException(
                    "Error de Gemini: HTTP " + e.getStatusCode().value()
            );
        } catch (ReglaNegocioException e) {
            throw e;
        } catch (Exception e) {
            throw new ReglaNegocioException(
                    "No se pudo interpretar el comprobante"
            );
        }
    }

    private String construirPrompt(String ocrText) {
        return """
                Eres un asistente financiero. Analiza el texto OCR de un
                comprobante peruano y devuelve exclusivamente un JSON válido
                con esta estructura:

                {
                  "razonamiento": "explicación breve",
                  "tipoDocumento": "boleta|factura|recibo|voucher|otro",
                  "comercio": "nombre del comercio",
                  "ruc": "RUC o null",
                  "fecha": "fecha ISO 8601 o null",
                  "moneda": "PEN|USD",
                  "subtotal": 0.0,
                  "igv": 0.0,
                  "total": 0.0,
                  "categoriaSugerida": "Alimentación|Servicios|Transporte|Salud|Ocio|Otros",
                  "confianza": 0.95,
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
                - Si un dato no está disponible, utiliza null cuando corresponda.
                - Los importes deben ser números, no cadenas de texto.
                - La confianza debe ser un número entre 0 y 1.
                - Responde únicamente con el JSON, sin bloques Markdown.

                Texto OCR:
                """ + ocrText;
    }

    private InterpretarComprobanteResponseDTO parsearRespuesta(
            String response) throws Exception {

        JsonNode root = objectMapper.readTree(response);
        JsonNode candidates = root.path("candidates");

        if (!candidates.isArray() || candidates.isEmpty()) {
            throw new ReglaNegocioException(
                    "Gemini no devolvió resultados para el comprobante"
            );
        }

        JsonNode parts = candidates.get(0)
                .path("content")
                .path("parts");

        if (!parts.isArray() || parts.isEmpty()
                || !parts.get(0).hasNonNull("text")) {
            throw new ReglaNegocioException(
                    "La respuesta de Gemini no contiene texto válido"
            );
        }

        String textoJson = parts.get(0).get("text").asText();
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

        if (valor == null || valor.isNull() || valor.asText().isBlank()) {
            return null;
        }

        return valor.asText();
    }

    private BigDecimal decimalOpcional(JsonNode data, String campo) {
        JsonNode valor = data.get(campo);

        if (valor == null || valor.isNull()
                || valor.asText().isBlank()
                || !valor.isNumber() && !valor.isTextual()) {
            return null;
        }

        try {
            return new BigDecimal(valor.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
