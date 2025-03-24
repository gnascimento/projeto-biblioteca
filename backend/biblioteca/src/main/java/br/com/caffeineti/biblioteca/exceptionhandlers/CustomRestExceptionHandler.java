package br.com.caffeineti.biblioteca.exceptionhandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Um erro desconhecido ocorreu.");
        body.put("details", ex.getMessage());
        log.error("Erro desconhecido", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintExceptions(RepositoryConstraintViolationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        ex.getErrors().getFieldErrors().forEach(fieldError -> {
            Map<String, Object> error = new HashMap<>();
            error.put("entity", fieldError.getObjectName());
            error.put("property", fieldError.getField());
            error.put("invalidValue", fieldError.getRejectedValue());
            error.put("message", fieldError.getDefaultMessage());
            errors.add(error);
        });

        body.put("errors", errors);
        log.info("Violação de restrição do repositório: {}", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}