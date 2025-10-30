package com.digi.common.exception;


import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Invalid request");
        log.warn("Validation error: {}", errorMessage);
        GenericResponse<Object> response = GenericResponse.error(AppConstant.GEN_ERROR_CODE, errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
}