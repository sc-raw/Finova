
package com.finova.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            RecursoNoEncontradoException ex) {
        return construirRespuesta(
                HttpStatus.NOT_FOUND, ex.getMessage()
        );
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(
            ReglaNegocioException ex) {
        return construirRespuesta(
                HttpStatus.BAD_REQUEST, ex.getMessage()
        );
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> manejarCredenciales(
            CredencialesInvalidasException ex) {
        return construirRespuesta(
                HttpStatus.UNAUTHORIZED, ex.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridadDatos(
            DataIntegrityViolationException ex) {

        String mensaje = "No se pudo guardar el registro porque "
                + "incumple una restricción de los datos";

        Throwable causa = ex;
        while (causa != null) {
            String detalle = causa.getMessage();

            if (detalle != null &&
                    detalle.toLowerCase().contains("duplicate entry")) {
                mensaje = "Ya existe un registro con ese valor";
                break;
            }

            causa = causa.getCause();
        }

        return construirRespuesta(
                HttpStatus.BAD_REQUEST, mensaje
        );
    }


	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> manejarTipoParametroInvalido(
	        MethodArgumentTypeMismatchException ex) {
	
	    return construirRespuesta(
	            HttpStatus.BAD_REQUEST,
	            "El parámetro '" + ex.getName() + "' tiene un formato inválido"
	    );
	}


    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status, String mensaje) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);

        return ResponseEntity.status(status).body(body);
    }
    

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(
	        IllegalArgumentException ex) {
	
	    return construirRespuesta(
	            HttpStatus.BAD_REQUEST,
	            ex.getMessage()
	    );
	}
	
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> manejarJsonInvalido(
	        HttpMessageNotReadableException ex) {
	    return construirRespuesta(
	            HttpStatus.BAD_REQUEST,
	            "El cuerpo de la petición tiene un formato inválido"
	    );
	}

	@ExceptionHandler(NumberFormatException.class)
	public ResponseEntity<Map<String, Object>> manejarNumeroInvalido(
	        NumberFormatException ex) {
	    return construirRespuesta(
	            HttpStatus.BAD_REQUEST,
	            "Formato numérico inválido: " + ex.getMessage()
	    );
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> manejarGenerico(Exception ex) {
	    ex.printStackTrace(); // para que veas el error real en consola
	    return construirRespuesta(
	            HttpStatus.INTERNAL_SERVER_ERROR,
	            "Error interno: " + ex.getMessage()
	    );
	}

}
