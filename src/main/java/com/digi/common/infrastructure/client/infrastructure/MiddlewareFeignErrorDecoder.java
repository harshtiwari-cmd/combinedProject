package com.digi.common.infrastructure.client.infrastructure;

import com.digi.common.infrastructure.exception.*;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Custom error decoder for Feign client
 * Converts Feign exceptions to appropriate custom exceptions
 */
@Slf4j
public class MiddlewareFeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus httpStatus = HttpStatus.resolve(response.status());
        
        if (httpStatus == null) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String errorMessage = extractErrorMessage(response);
        
        log.error("Feign client error: status={}, method={}, message={}", 
                httpStatus, methodKey, errorMessage);

        // Convert to appropriate custom exception based on HTTP status
        String fullErrorMessage = "Middleware service error: " + errorMessage;

        return switch (httpStatus) {
            case BAD_REQUEST -> new BadRequestException(fullErrorMessage);
            case NOT_FOUND -> new ResourceNotFoundException(fullErrorMessage);
            case REQUEST_TIMEOUT, GATEWAY_TIMEOUT -> new RequestTimeoutException(fullErrorMessage);
            case SERVICE_UNAVAILABLE -> new ServiceUnavailableException(fullErrorMessage);
            case INTERNAL_SERVER_ERROR -> new InternalServerException(fullErrorMessage);
            default -> new MiddlewareException(fullErrorMessage, httpStatus);
        };
    }

    /**
     * Extract error message from response body
     */
    private String extractErrorMessage(Response response) {
        try {
            if (response.body() != null) {
                InputStream inputStream = response.body().asInputStream();
                byte[] bodyBytes = inputStream.readAllBytes();
                return new String(bodyBytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.warn("Failed to read error response body", e);
        }
        
        return response.reason() != null ? response.reason() : "Unknown error";
    }
}