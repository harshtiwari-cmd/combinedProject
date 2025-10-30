package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.NotificationService;
import com.digi.common.domain.model.StandardErrorCode;
import com.digi.common.domain.model.dto.*;
import com.digi.common.infrastructure.client.infrastructure.MiddlewareClientService;
import com.digi.common.infrastructure.exception.BadRequestException;
import com.digi.common.infrastructure.exception.BaseApplicationException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService
 * 
 * This service follows the Single Responsibility Principle by focusing
 * solely on notification sending operations. It also follows the
 * Dependency Inversion Principle by depending on abstractions.
 * 
 * Now calls dkn-middleware-service instead of directly calling Bank MQ
 */
@Service
@AllArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    /**
     * Request-level field mapping configuration
     * Maps top-level request fields to middleware parameters
     */
    private static final List<RequestFieldMapping> REQUEST_FIELD_MAPPINGS;
    
    /**
     * Parameter-level field mapping configuration
     * Maps parameter array fields to middleware parameters
     */
    private static final Map<String, List<String>> PARAMETER_FIELD_MAPPINGS;
    
    static {
        // Request-level mappings (from NotificationRequestDto properties)
        List<RequestFieldMapping> requestMappings = new ArrayList<>();
        
        // notificationId -> serviceNumber
        requestMappings.add(new RequestFieldMapping(
                NotificationRequestDto::getNotificationId,
                List.of("serviceNumber")
        ));
        
        // correlationId -> referenceNumber and referenceNumConsumer (dual mapping)
        requestMappings.add(new RequestFieldMapping(
                NotificationRequestDto::getCorrelationId,
                List.of("referenceNumber", "referenceNumConsumer")
        ));
        
        REQUEST_FIELD_MAPPINGS = Collections.unmodifiableList(requestMappings);
        
        // Parameter-level mappings (from NotificationRequestDto.parameters array)
        Map<String, List<String>> paramMappings = new HashMap<>();
        
        // Pass-through fields - map to themselves unchanged
        paramMappings.put("smsText", List.of("smsText"));
        paramMappings.put("backend", List.of("backend"));
        
        // Dual mapping - customerNumber goes to TWO target fields
        paramMappings.put("customerNumber", List.of("customerNumber", "smsParameters.parameter3"));
        
        // Single mappings - each source field maps to ONE target field
        paramMappings.put("sourceAccountNumber", List.of("smsParameters.parameter2"));
        paramMappings.put("currency", List.of("smsParameters.parameter4"));
        paramMappings.put("transactionAmount", List.of("smsParameters.parameter5"));
        paramMappings.put("transactionDatetime", List.of("smsParameters.parameter6"));
        paramMappings.put("transactionDescription", List.of("smsParameters.parameter7"));
        paramMappings.put("availableBalance", List.of("smsParameters.parameter8"));
        paramMappings.put("customerName", List.of("smsParameters.parameter9"));
        paramMappings.put("accountType", List.of("smsParameters.parameter10"));
        paramMappings.put("destinationAccountNumber", List.of("smsParameters.parameter11"));
        paramMappings.put("callCenterNumber", List.of("smsParameters.parameter12"));
        paramMappings.put("mobileNumber", List.of("smsParameters.parameter13"));
        paramMappings.put("cardPaddedNumber", List.of("smsParameters.parameter14"));
        
        PARAMETER_FIELD_MAPPINGS = Collections.unmodifiableMap(paramMappings);
    }
    
    /**
     * Helper class to define request-level field mappings
     */
    private static class RequestFieldMapping {
        private final Function<NotificationRequestDto, String> valueExtractor;
        private final List<String> targetFields;
        
        RequestFieldMapping(Function<NotificationRequestDto, String> valueExtractor, List<String> targetFields) {
            this.valueExtractor = valueExtractor;
            this.targetFields = targetFields;
        }
        
        void apply(NotificationRequestDto request, List<FieldValueDto> parameters) {
            String value = valueExtractor.apply(request);
            if (value != null && !value.trim().isEmpty()) {
                targetFields.forEach(target -> 
                    parameters.add(new FieldValueDto(target, value)));
            }
        }
    }

    private MiddlewareClientService middlewareClientService;

    @Override
    public SendNotificationResponseDto sendNotification(NotificationRequestDto request) {
        String correlationId = request.getCorrelationId();
        
        try {
            log.info("Processing SMS notification request: notificationId={}, correlationId={}, language={}", 
                    request.getNotificationId(), correlationId, request.getLanguage());

            // Validate SMS-specific request
            validateSmsRequest(request);

            // Transform to middleware request format
            MiddlewareRequestDto middlewareRequest = buildMiddlewareRequest(request);
            
            log.info("Sending SMS via middleware: correlationId={}, parameters={}", 
                    correlationId, request.getParameters().size());

            // Call middleware service (which handles MQ communication)
            MiddlewareResponseDto middlewareResponse = middlewareClientService.sendRequest(middlewareRequest);

            // Transform response back to notification response format
            SendNotificationResponseDto response = transformMiddlewareResponse(middlewareResponse, correlationId);
            
            log.info("SMS notification completed: correlationId={}, status={}", 
                    correlationId, response.getStatus());
            
            return response;

        } catch (BaseApplicationException e) {
            log.error("Application error processing SMS notification: correlationId={}, status={}, message={}", 
                    correlationId, e.getHttpStatus(), e.getMessage());
            return buildErrorResponseFromException(e, correlationId);

        } catch (Exception e) {
            log.error("Unexpected error processing SMS notification: correlationId={}", correlationId, e);
            return buildErrorResponse(
                    StandardErrorCode.INTERNAL_SERVER_ERROR,
                    "Failed to send SMS notification. Please try again later.", 
                    correlationId, 
                    null);
        }
    }

    /**
     * Validate SMS-specific request
     */
    private void validateSmsRequest(NotificationRequestDto request) {
        if (request.getNotificationId() == null || request.getNotificationId().trim().isEmpty()) {
            throw new BadRequestException("Notification ID is required");
        }
        
        if (request.getParameters() == null || request.getParameters().isEmpty()) {
            throw new BadRequestException("SMS parameters are required");
        }
        
        // Check for required SMS fields
        //TODO: handle mobileNumber v/s customerNumber
        boolean hasMobileNumber = request.getParameters().stream()
                .anyMatch(p -> "mobileNumber".equals(p.getFieldName()) && 
                               p.getFieldValue() != null && 
                               !p.getFieldValue().trim().isEmpty());
        
        if (!hasMobileNumber) {
            log.warn("SMS request missing mobile number: correlationId={}", request.getCorrelationId());
        }
    }

    /**
     * Build middleware request from notification request
     * Fully generic - all mappings driven by configuration
     */
    private MiddlewareRequestDto buildMiddlewareRequest(NotificationRequestDto request) {
        List<FieldValueDto> middlewareParameters = new ArrayList<>();
        
        // Process request-level fields (notificationId, correlationId, etc.)
        REQUEST_FIELD_MAPPINGS.forEach(mapping -> mapping.apply(request, middlewareParameters));
        
        // Process parameter array fields
        Optional.ofNullable(request.getParameters())
                .ifPresent(params -> params.forEach(param -> 
                    processParameter(middlewareParameters, param)));
        
        return MiddlewareRequestDto.builder()
                .serviceName("SEND.SMS")
                .parameters(middlewareParameters)
                .correlationId(request.getCorrelationId())
                .language(request.getLanguage() != null ? request.getLanguage() : "E")
                .build();
    }
    
    /**
     * Process individual parameter with completely uniform logic
     * Handles pass-through, single, and dual mappings with the same code path
     */
    private void processParameter(List<FieldValueDto> parameters, FieldValueDto param) {
        String fieldName = param.getFieldName();
        String fieldValue = param.getFieldValue();
        
        // Skip empty values early
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            return;
        }
        
        // Handle all field mappings uniformly (pass-through, single, or dual)
        List<String> targetFields = PARAMETER_FIELD_MAPPINGS.get(fieldName);
        if (targetFields != null) {
            // Add parameter for each target field
            // Works for: pass-through (1 target), single mapping (1 target), dual mapping (2+ targets)
            targetFields.forEach(target -> 
                parameters.add(new FieldValueDto(target, fieldValue)));
        } else {
            // Unknown field - add as is with warning
            log.warn("Unknown parameter field '{}' not in mapping, adding as is", fieldName);
            parameters.add(new FieldValueDto(fieldName, fieldValue));
        }
    }

    /**
     * Transform middleware response to notification response
     */
    private SendNotificationResponseDto transformMiddlewareResponse(
            MiddlewareResponseDto middlewareResponse, String correlationId) {
        
        if ("SUCCESS".equalsIgnoreCase(middlewareResponse.getStatus())) {
            // Extract bank response from JsonNode
            SendNotificationResponseDto.BankResponse bankResponse = extractBankResponse(middlewareResponse.getBankResponse());
            
            return SendNotificationResponseDto.builder()
                    .responseCode(StandardErrorCode.SUCCESS.getCode())
                    .status("SUCCESS")
                    .message(middlewareResponse.getMessage())
                    .timestamp(middlewareResponse.getTimestamp())
                    .details(SendNotificationResponseDto.NotificationDetails.builder()
                            .bankResponse(bankResponse)
                            .build())
                    .build();
        } else {
            // Handle error response - determine error code from middleware errors
            StandardErrorCode errorCode = StandardErrorCode.INTERNAL_SERVER_ERROR;
            List<SendNotificationResponseDto.ValidationError> errors = null;
            
            if (middlewareResponse.getErrors() != null) {
                errors = middlewareResponse.getErrors().stream()
                        .map(e -> SendNotificationResponseDto.ValidationError.builder()
                                .fieldName(e.getFieldName())
                                .errorCode(e.getErrorCode())
                                .errorMessage(e.getErrorMessage())
                                .providedValue(e.getProvidedValue())
                                .build())
                        .collect(Collectors.toList());
                
                // Determine error code from first error
                if (!errors.isEmpty()) {
                    String firstErrorCode = errors.get(0).getErrorCode();
                    if ("SERVICE_UNAVAILABLE".equals(firstErrorCode)) {
                        errorCode = StandardErrorCode.SERVICE_UNAVAILABLE;
                    } else if ("TIMEOUT".equals(firstErrorCode) || "REQUEST_TIMEOUT".equals(firstErrorCode)) {
                        errorCode = StandardErrorCode.REQUEST_TIMEOUT;
                    } else if (firstErrorCode != null && firstErrorCode.startsWith("4")) {
                        errorCode = StandardErrorCode.BAD_REQUEST;
                    }
                }
            }
            
            return buildErrorResponse(errorCode, middlewareResponse.getMessage(), correlationId, errors);
        }
    }

    /**
     * Extract bank response details from JsonNode
     * Maps SendSMSReply structure to BankResponse DTO
     */
    private SendNotificationResponseDto.BankResponse extractBankResponse(JsonNode bankResponseNode) {
        if (bankResponseNode == null) {
            return null;
        }

        // Extract returnStatus object (contains returnCode and returnCodeDesc)
        String returnCode = null;
        String returnMessage = null;
        
        if (bankResponseNode.has("returnStatus")) {
            JsonNode returnStatusNode = bankResponseNode.get("returnStatus");
            if (returnStatusNode != null && returnStatusNode.isObject()) {
                returnCode = returnStatusNode.has("returnCode") ? 
                        returnStatusNode.get("returnCode").asText() : null;
                returnMessage = returnStatusNode.has("returnCodeDesc") ? 
                        returnStatusNode.get("returnCodeDesc").asText() : null;
            }
        }

        // Extract reference number (field name is referenceNum in SendSMSReply)
        String referenceNumber = bankResponseNode.has("referenceNum") ? 
                bankResponseNode.get("referenceNum").asText() : null;

        return SendNotificationResponseDto.BankResponse.builder()
                .returnStatus(returnCode != null ? returnCode : "")
                .returnCode(returnCode)
                .returnMessage(returnMessage)
                .referenceNumber(referenceNumber)
                .build();
    }

    private SendNotificationResponseDto buildSuccessResponse(NotificationRequestDto request,
                                                               String correlationId,
                                                               SendNotificationResponseDto.BankResponse bankResponse) {
        
        return SendNotificationResponseDto.builder()
                .responseCode(StandardErrorCode.SUCCESS.getCode())
                .status("SUCCESS")
                .message("Notification sent successfully")
                .timestamp(LocalDateTime.now())
                .details(SendNotificationResponseDto.NotificationDetails.builder()
                        .bankResponse(bankResponse)
                        .build())
                .build();
    }

    private SendNotificationResponseDto buildErrorResponse(StandardErrorCode errorCode,
                                                         String message, 
                                                         String correlationId,
                                                         List<SendNotificationResponseDto.ValidationError> errors) {
        
        return SendNotificationResponseDto.builder()
                .responseCode(errorCode.getCode())
                .status("ERROR")
                .message(message)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }

    /**
     * Build error response from BaseApplicationException
     */
    private SendNotificationResponseDto buildErrorResponseFromException(BaseApplicationException e, String correlationId) {
        StandardErrorCode errorCode;
        List<SendNotificationResponseDto.ValidationError> errors = null;
        
        // Map HTTP status to standard error code
        if (e.getHttpStatus() == HttpStatus.SERVICE_UNAVAILABLE) {
            errorCode = StandardErrorCode.SERVICE_UNAVAILABLE;
            errors = List.of(
                SendNotificationResponseDto.ValidationError.builder()
                    .fieldName("middleware")
                    .errorCode(StandardErrorCode.SERVICE_UNAVAILABLE.getCode())
                    .errorMessage("SMS service is temporarily unavailable")
                    .providedValue("Please try again later")
                    .build()
            );
        } else if (e.getHttpStatus() == HttpStatus.GATEWAY_TIMEOUT || e.getHttpStatus() == HttpStatus.REQUEST_TIMEOUT) {
            errorCode = StandardErrorCode.REQUEST_TIMEOUT;
            errors = List.of(
                SendNotificationResponseDto.ValidationError.builder()
                    .fieldName("timeout")
                    .errorCode(StandardErrorCode.REQUEST_TIMEOUT.getCode())
                    .errorMessage("Request timeout while sending SMS")
                    .providedValue("Please try again")
                    .build()
            );
        } else if (e.getHttpStatus() == HttpStatus.BAD_REQUEST) {
            errorCode = StandardErrorCode.BAD_REQUEST;
            errors = List.of(
                SendNotificationResponseDto.ValidationError.builder()
                    .fieldName("request")
                    .errorCode(StandardErrorCode.BAD_REQUEST.getCode())
                    .errorMessage(e.getMessage())
                    .providedValue(null)
                    .build()
            );
        } else if (e.getHttpStatus() == HttpStatus.NOT_FOUND) {
            errorCode = StandardErrorCode.NO_DATA_FOUND;
        } else {
            errorCode = StandardErrorCode.INTERNAL_SERVER_ERROR;
        }
        
        return SendNotificationResponseDto.builder()
                .responseCode(errorCode.getCode())
                .status("ERROR")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
    }
}
