package com.api.alquilaya.controllers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Convierte las excepciones en respuestas JSON claras en lugar de un error 500
@RestControllerAdvice
public class ApiExceptionHandler {

    // Errores de validación (@NotBlank, @Email, @Positive...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldError() == null
                ? "Solicitud inválida"
                : ex.getBindingResult().getFieldError().getDefaultMessage();
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Reglas de negocio que no se cumplen (lanzadas desde los services)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> reglaDeNegocio(IllegalArgumentException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // JSON mal escrito o con tipos incorrectos (por ejemplo, una fecha con formato inválido)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo JSON no es válido. Revisa el formato (fechas: yyyy-MM-dd)");
    }

    // Violaciones de la base de datos (datos únicos repetidos, registros relacionados, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridad(DataIntegrityViolationException ex) {
        return respuesta(HttpStatus.CONFLICT, "La operación viola una restricción de la base de datos (dato duplicado o registro relacionado)");
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}
