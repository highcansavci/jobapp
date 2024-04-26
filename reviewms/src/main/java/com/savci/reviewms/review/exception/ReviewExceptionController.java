package com.savci.reviewms.review.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class ReviewExceptionController {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleConstraintValidation(ConstraintViolationException ex) {
        log.error("Constraints of the entities are not met!");
        List<String> errors = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
        return new ResponseEntity<>(buildValidationErrors(errors), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleMalformedBodyException(HttpMessageNotReadableException ex) {
        log.error("Required company body is malformed!");
        return new ResponseEntity<>("Required review body is malformed!", HttpStatus.BAD_REQUEST);
    }

    private Map<String, List<String>> buildValidationErrors(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}

