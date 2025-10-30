package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.NotificationService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.BaseRequest;
import com.digi.common.domain.model.dto.BaseResponse;
import com.digi.common.domain.model.dto.NotificationRequestDto;
import com.digi.common.domain.model.dto.SendNotificationResponseDto;
import com.digi.common.infrastructure.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller for sending notifications
 * <p>
 * This controller follows the Single Responsibility Principle by focusing
 * solely on notification sending operations.
 * Exception handling is delegated to GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Slf4j
public class NotificationController {

    private NotificationService notificationService;

    /**
     * Send a notification based on the provided request
     *
     * @param request the base request containing requestInfo and deviceInfo
     * @param serviceId the service ID making the request
     * @param moduleId the module ID within the service
     * @param subModuleId the sub-module ID
     * @param screenId the screen ID making the request
     * @param channel the channel (WEB, MOBILE, etc.)
     * @param acceptLanguage the accept language preference (en/ar) - used for both response and middleware
     * @param correlationId the correlation ID for tracking (optional)
     * @return standardized response containing the result of the notification sending operation
     */
    @PostMapping("/send-notifications")
    public ResponseEntity<BaseResponse<SendNotificationResponseDto.BankResponse>> sendNotification(
            @Valid @RequestBody BaseRequest request,
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage,
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId) {

        log.info("Received send notification request: notificationId={}, serviceId={}, moduleId={}, subModuleId={}, screenId={}, channel={}, acceptLanguage={}", 
                request.getRequestInfo().getNotificationId(), serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage);

        // Convert Accept-Language to middleware format (en -> EN, ar -> AR)
        String middlewareLanguage = convertToMiddlewareLanguage(acceptLanguage);
        
        // Extract data from BaseRequest and create NotificationRequestDto
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .notificationId(request.getRequestInfo().getNotificationId())
                .parameters(request.getRequestInfo().getParameters())
                .language(middlewareLanguage)
                .build();

        // Set correlation ID (generate if not provided)
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            log.info("Generated correlation ID: {}", correlationId);
        }
        notificationRequest.setCorrelationId(correlationId);

        // Send notification (exceptions handled by GlobalExceptionHandler)
        SendNotificationResponseDto response = notificationService.sendNotification(notificationRequest);

        // Use ResponseBuilder to handle response code mapping
        return ResponseBuilder.fromNotificationResponse(response);
    }

    /**
     * Convert Accept-Language header value to middleware language format
     * 
     * @param acceptLanguage the Accept-Language header value (en, ar, en-US, ar-SA, etc.)
     * @return middleware language code (EN or AR)
     */
    private String convertToMiddlewareLanguage(String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.trim().isEmpty()) {
            return "EN"; // Default to English
        }
        
        String lang = acceptLanguage.toLowerCase().trim();
        
        // Handle various formats: ar, ar-SA, arabic
        if (lang.startsWith("ar") || lang.equals("arabic")) {
            return "AR";
        }
        
        // Default to English for en, en-US, english, or any unrecognized value
        return "EN";
    }
}