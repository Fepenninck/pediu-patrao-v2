package com.umc.pediupatrao.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = DashboardController.class)
public class DashboardExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(DashboardExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> validacao(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("status", 400, "mensagem", error.getMessage()));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> parametros(Exception error) {
        return ResponseEntity.badRequest().body(Map.of("status", 400,
                "mensagem", "Informe datas válidas no formato yyyy-MM-dd e limite inteiro entre 1 e 50."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> falha(Exception error) {
        log.error("Falha ao consultar dashboard", error);
        return ResponseEntity.internalServerError().body(Map.of("status", 500,
                "mensagem", "Não foi possível consultar os dados. Tente novamente mais tarde."));
    }
}
