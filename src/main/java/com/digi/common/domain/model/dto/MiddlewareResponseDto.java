package com.digi.common.domain.model.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for middleware service response
 * Matches the MqResponseDto format from dkn-middleware-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiddlewareResponseDto {
    
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private JsonNode bankResponse;
    private List<ValidationError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String fieldName;
        private String errorCode;
        private String errorMessage;
        private String providedValue;
    }
}