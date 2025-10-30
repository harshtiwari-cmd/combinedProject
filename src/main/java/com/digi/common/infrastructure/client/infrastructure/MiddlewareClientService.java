package com.digi.common.infrastructure.client.infrastructure;

import com.digi.common.domain.model.dto.MiddlewareRequestDto;
import com.digi.common.domain.model.dto.MiddlewareResponseDto;
import com.digi.common.infrastructure.exception.*;
import com.digi.common.infrastructure.util.MessageSourceService;
import feign.FeignException;
import feign.RetryableException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.SocketTimeoutException;

/**
 * Service for calling dkn-middleware-service REST API using Feign Client
 */
@Service
@AllArgsConstructor
@Slf4j
public class MiddlewareClientService {
    
    private MiddlewareFeignClient middlewareFeignClient;
    private MessageSourceService messageSourceService;

    /**
     * Send request to middleware service
     * 
     * @param request The middleware request
     * @return Response from middleware service
     */
    public MiddlewareResponseDto sendRequest(MiddlewareRequestDto request) {
        try {
            log.info("Calling middleware service via Feign client");
            log.info("Request payload: serviceName={}, correlationId={}, language={}", 
                    request.getServiceName(), request.getCorrelationId(), request.getLanguage());

            // Validate and normalize language code
            String language = normalizeLanguage(request.getLanguage());
            String correlationId = request.getCorrelationId();

            // Validate request before sending
            validateRequest(request);

            // Call middleware service via Feign client
            MiddlewareResponseDto response = middlewareFeignClient.sendRequest(
                    correlationId,
                    language,
                    request
            );

            log.info("Received response from middleware via Feign");
            
            if (response != null) {
                log.info("Response body: status={}, message={}", 
                        response.getStatus(), response.getMessage());
                
                // Validate response
                validateResponse(response);
            } else {
                log.warn("Received null response from middleware service");
                throw new InternalServerException(
                        messageSourceService.getMessage("error.middleware.null.response"));
            }

            return response;

        } catch (FeignException.ServiceUnavailable e) {
            log.error("Middleware service unavailable (503): {}", e.getMessage());
            throw new ServiceUnavailableException(
                    messageSourceService.getMessage("error.middleware.unavailable"), e);
                    
        } catch (FeignException.GatewayTimeout e) {
            log.error("Gateway timeout calling middleware service: {}", e.getMessage());
            throw new RequestTimeoutException(
                    messageSourceService.getMessage("error.middleware.timeout"), e);
                    
        } catch (RetryableException e) {
            log.error("Retryable error calling middleware service: {}", e.getMessage());
            throw new ServiceUnavailableException(
                    messageSourceService.getMessage("error.middleware.connection"), e);
                    
        } catch (FeignException e) {
            log.error("Feign client error calling middleware service: status={}, message={}", 
                    e.status(), e.getMessage(), e);
                    
            HttpStatus status = HttpStatus.resolve(e.status());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            
            String errorMessage = extractErrorMessage(e);
            throw new MiddlewareException(errorMessage, status);
            
        } catch (BaseApplicationException e) {
            // Re-throw custom exceptions as-is
            throw e;
            
        } catch (Exception e) {
            log.error("Unexpected error calling middleware service", e);
            
            // Check if it's a connection error
            if (isConnectionError(e)) {
                throw new ServiceUnavailableException(
                        messageSourceService.getMessage("error.middleware.connection") + ": " + e.getMessage(), e);
            }
            
            throw new MiddlewareException(
                    messageSourceService.getMessage("error.middleware.error") + ": " + e.getMessage(), e);
        }
    }

    /**
     * Normalize language code to middleware format (case insensitive)
     * 
     * Converts various language code formats to middleware's expected format:
     * - Arabic: ar, AR, Ar, aR, arabic, ARABIC → "A"
     * - English: en, EN, En, eN, english, ENGLISH → "E"
     * - Legacy: e, E → "E", a, A → "A"
     * 
     * @param language The language code in any case
     * @return "E" for English or "A" for Arabic (defaults to "E" if unrecognized)
     */
    private String normalizeLanguage(String language) {
        if (language == null || language.trim().isEmpty()) {
            return "E"; // Default to English
        }
        
        // Convert to uppercase for case-insensitive comparison
        String upperLang = language.toUpperCase().trim();
        
        // Map English variants (en, EN, En, eN, english, ENGLISH, e, E)
        if ("EN".equals(upperLang) || "ENGLISH".equals(upperLang) || "E".equals(upperLang)) {
            return "E";
        }
        
        // Map Arabic variants (ar, AR, Ar, aR, arabic, ARABIC, a, A)
        if ("AR".equals(upperLang) || "ARABIC".equals(upperLang) || "A".equals(upperLang)) {
            return "A";
        }
        
        // Default to English for any unrecognized code
        log.warn("Unknown language code '{}', defaulting to English", language);
        return "E";
    }

    /**
     * Validate request before sending
     */
    private void validateRequest(MiddlewareRequestDto request) {
        if (request == null) {
            throw new BadRequestException(messageSourceService.getMessage("error.middleware.invalid.request"));
        }
        
        if (request.getServiceName() == null || request.getServiceName().trim().isEmpty()) {
            throw new BadRequestException(messageSourceService.getMessage("error.middleware.service.name.required"));
        }
        
        if (request.getCorrelationId() == null || request.getCorrelationId().trim().isEmpty()) {
            throw new BadRequestException(messageSourceService.getMessage("error.middleware.correlation.id.required"));
        }
        
        if (request.getParameters() == null || request.getParameters().isEmpty()) {
            log.warn("Request has no parameters: correlationId={}", request.getCorrelationId());
        }
    }

    /**
     * Validate response from middleware
     */
    private void validateResponse(MiddlewareResponseDto response) {
        if (response.getStatus() == null) {
            log.warn("Response has no status field");
        }
        
        if ("ERROR".equalsIgnoreCase(response.getStatus())) {
            log.warn("Middleware returned error status: {}", response.getMessage());
        }
    }

    /**
     * Extract meaningful error message from FeignException
     */
    private String extractErrorMessage(FeignException e) {
        String content = e.contentUTF8();
        
        if (content != null && !content.trim().isEmpty()) {
            // Try to extract message from JSON response
            if (content.contains("\"message\"")) {
                try {
                    int start = content.indexOf("\"message\"") + 11;
                    int end = content.indexOf("\"", start);
                    if (end > start) {
                        return content.substring(start, end);
                    }
                } catch (Exception ex) {
                    log.warn("Failed to extract error message from response", ex);
                }
            }
            return messageSourceService.getMessage("error.middleware.error") + ": " + content;
        }
        
        return messageSourceService.getMessage("error.middleware.error") + ": HTTP " + e.status();
    }

    /**
     * Check if exception is a connection error
     */
    private boolean isConnectionError(Exception e) {
        if (e instanceof SocketTimeoutException) {
            return true;
        }
        
        String message = e.getMessage();
        if (message == null) {
            return false;
        }
        
        return message.contains("Connection refused") ||
               message.contains("Connection reset") ||
               message.contains("No route to host") ||
               message.contains("Network is unreachable") ||
               message.contains("UnknownHostException");
    }
}




