package com.autobots.automanager.controles;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrosApi extends ResponseEntityExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> conflito(DataIntegrityViolationException ex, WebRequest request) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "Conflito de dados: verifique o numero unico do documento e os vinculos.");
        return handleExceptionInternal(ex, problema, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        var problema = ProblemDetail.forStatusAndDetail(status, "Dados invalidos.");
        problema.setProperty("campos", ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage()).toList());
        return handleExceptionInternal(ex, problema, headers, status, request);
    }
}
