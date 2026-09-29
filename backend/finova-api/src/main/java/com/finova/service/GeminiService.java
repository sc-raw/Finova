package com.finova.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finova.dto.InterpretarComprobanteResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parsearRespuesta(response);
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a Gemini: " + e.getMessage(), e);
        }
    }

    private String construirPrompt(String ocrText) {
        return """
            Eres un asistente financiero. Analiza el siguiente texto extraído por OCR de una boleta o comprobante peruano y devuelve un JSON con esta estructura exacta:
            {
              "razonamiento": "explicación breve de por qué llegaste a esa conclusión",
              "tipoDocumento": "boleta|factura|recibo|voucher|otro",
              "comercio": "nombre del comercio",
              "ruc": "RUC si lo encuentras",
              "fecha": "fecha en formato ISO",
              "moneda": "PEN|USD",
              "subtotal": 0.0,
              "igv": 0.0,
              "total": 0.0,
              "categoriaSugerida": "Alimentación|Servicios|Transporte|Salud|Ocio|Otros",
              "confianza": 0.95,
              "items": [
                {"nombre": "producto", "cantidad": 1, "precio": 0.0}
              ]
            }
            Responde SOLO con el JSON, sin texto adicional.

            Texto OCR:
            """ + ocrText;
    }

    private InterpretarComprobanteResponseDTO parsearRespuesta(String response) throws Exception {
        JsonNode root = objectMapper.readTree(response);
        JsonNode candidates = root.path("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("Gemini no devolvió candidatos");
        }
        String textoJson = candidates.get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        JsonNode data = objectMapper.readTree(textoJson);

        InterpretarComprobanteResponseDTO dto = new InterpretarComprobanteResponseDTO();
        dto.setRazonamiento(data.path("razonamiento").asText());
        dto.setTipoDocumento(data.path("tipoDocumento").asText());
        dto.setComercio(data.path("comercio").asText());
        dto.setRuc(data.path("ruc").asText(null));
        dto.setFecha(data.path("fecha").asText(null));
        dto.setMoneda(data.path("moneda").asText("PEN"));
        dto.setSubtotal(data.has("subtotal") ? new BigDecimal(data.get("subtotal").asText()) : null);
        dto.setIgv(data.has("igv") ? new BigDecimal(data.get("igv").asText()) : null);
        dto.setTotal(data.has("total") ? new BigDecimal(data.get("total").asText()) : null);
        dto.setCategoriaSugerida(data.path("categoriaSugerida").asText());
        dto.setConfianza(data.has("confianza") ? new BigDecimal(data.get("confianza").asText()) : null);

        List<InterpretarComprobanteResponseDTO.ItemDTO> items = new ArrayList<>();
        if (data.has("items") && data.get("items").isArray()) {
            for (JsonNode item : data.get("items")) {
                items.add(new InterpretarComprobanteResponseDTO.ItemDTO(
                        item.path("nombre").asText(),
                        item.path("cantidad").asInt(1),
                        item.has("precio") ? new BigDecimal(item.get("precio").asText()) : null
                ));
            }
        }
        dto.setItems(items);
        return dto;
    }
}