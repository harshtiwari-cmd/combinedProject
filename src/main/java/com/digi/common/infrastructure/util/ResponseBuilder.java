package com.digi.common.infrastructure.util;

import com.digi.common.domain.model.dto.BaseResponse;
import com.digi.common.domain.model.dto.SendNotificationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for building standardized API responses
 * Eliminates duplicate response building logic across controllers
 */
public final class ResponseBuilder {
    
    // Prevent instantiation
    private ResponseBuilder() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Build success response (200 OK)
     */
    public static <T> ResponseEntity<BaseResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(BaseResponse.success(data, message));
    }
    
    /**
     * Build success response with default message
     */
    public static <T> ResponseEntity<BaseResponse<T>> success(T data) {
        return ResponseEntity.ok(BaseResponse.success(data));
    }
    
    /**
     * Build bad request response (400)
     */
    public static <T> ResponseEntity<BaseResponse<T>> badRequest(String message) {
        return ResponseEntity.badRequest().body(BaseResponse.badRequest(message));
    }
    
    /**
     * Build not found response (404)
     */
    public static <T> ResponseEntity<BaseResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.notFound(message));
    }
    
    /**
     * Build timeout response (408)
     */
    public static <T> ResponseEntity<BaseResponse<T>> timeout(String message) {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(BaseResponse.timeout(message));
    }
    
    /**
     * Build service unavailable response (503)
     */
    public static <T> ResponseEntity<BaseResponse<T>> serviceUnavailable(String message) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(BaseResponse.serviceUnavailable(message));
    }
    
    /**
     * Build internal server error response (500)
     */
    public static <T> ResponseEntity<BaseResponse<T>> internalServerError(String message) {
        return ResponseEntity.internalServerError()
                .body(BaseResponse.error(message));
    }
    
    /**
     * Build response from SendNotificationResponseDto (handles response code mapping)
     */
    public static ResponseEntity<BaseResponse<SendNotificationResponseDto.BankResponse>> fromNotificationResponse(
            SendNotificationResponseDto response) {
        
        String responseCode = response.getResponseCode();
        String message = response.getMessage();
        
        // Extract bank response for data field
        SendNotificationResponseDto.BankResponse bankResponse = 
            response.getDetails() != null ? response.getDetails().getBankResponse() : null;
        
        // Map response code to appropriate HTTP status and BaseResponse
        return switch (responseCode) {
            case "000000" -> success(bankResponse, message);
            case "000400" -> badRequest(message);
            case "000404" -> notFound(message);
            case "000408" -> timeout(message);
            case "000503" -> serviceUnavailable(message);
            default -> ResponseEntity.internalServerError()
                    .body(BaseResponse.error(responseCode, message));
        };
    }
    
}
