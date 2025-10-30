package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for send notification response
 * 
 * This DTO follows the Interface Segregation Principle by providing
 * only the necessary data for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponseDto {

    private String responseCode;  // Standard error code (000000, 000400, etc.)
    private String status;         // SUCCESS or ERROR
    private String message;
    private LocalDateTime timestamp;
    private NotificationDetails details;
    private List<ValidationError> errors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationDetails {
        private BankResponse bankResponse;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankResponse {
        private String returnStatus;
        private String returnCode;
        private String returnMessage;
        private String referenceNumber;
    }

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
