package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.NotificationConfigurationService;
import com.digi.common.config.ErrorMessages;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.NotificationType;
import com.digi.common.domain.model.dto.BaseRequest;
import com.digi.common.domain.model.dto.BaseResponse;
import com.digi.common.domain.model.dto.NotificationTemplateResponseDto;
import com.digi.common.infrastructure.util.MessageSourceService;
import com.digi.common.infrastructure.util.ResponseBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for notification configuration management
 * Exception handling is delegated to GlobalExceptionHandler
 */
@RestController
@RequestMapping("/api/configuration")
@AllArgsConstructor
@Slf4j
public class NotificationConfigurationController {

    private NotificationConfigurationService configurationService;

    private MessageSourceService messageSourceService;

    /**
     * Get configuration for a specific notification id
     *
     * @param request the base request containing requestInfo, deviceInfo and notification configuration request
     * @param serviceId the service ID making the request
     * @param moduleId the module ID within the service
     * @param subModuleId the submodule ID
     * @param screenId the screen ID making the request
     * @param channel the channel (WEB, MOBILE, etc.)
     * @param acceptLanguage the accept language preference (en, ar)
     * @return configuration response containing use_case, parameterList and type
     */
    @PostMapping("/send-notification/get-configuration")
    public ResponseEntity<BaseResponse<NotificationTemplateResponseDto>> getNotificationConfiguration(
            @RequestBody BaseRequest request,
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        log.info("Received request for notification configuration: notificationId={}, serviceId={}, moduleId={}, channel={}",
                request.getRequestInfo().getNotificationId(), serviceId, moduleId, channel);

        NotificationTemplateResponseDto configuration = configurationService
                .getNotificationConfiguration(request.getRequestInfo().getNotificationId());

        return ResponseBuilder.success(configuration, messageSourceService.getMessage(ErrorMessages.CONFIGURATION_RETRIEVED));
    }

    /**
     * Get all notification templates for sending notifications
     *
     * @param request the base request containing requestInfo and deviceInfo
     * @param serviceId the service ID making the request
     * @param moduleId the module ID within the service
     * @param subModuleId the submodule ID
     * @param screenId the screen ID making the request
     * @param channel the channel (WEB, MOBILE, etc.)
     * @param acceptLanguage the accept language preference (en, ar)
     * @return list of all active notification templates
     */
    @PostMapping("/send-notification/get-all")
    public ResponseEntity<BaseResponse<List<NotificationTemplateResponseDto>>> getAllNotificationTemplates(
            @RequestBody BaseRequest request,
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        log.info("Received request for all notification templates: serviceId={}, channel={}", serviceId, channel);

        List<NotificationTemplateResponseDto> templates = configurationService.getAllTemplates();

        log.info("Retrieved {} notification templates", templates.size());

        return ResponseBuilder.success(templates, messageSourceService.getMessage(ErrorMessages.ALL_TEMPLATES_RETRIEVED));
    }

    /**
     * Get notification templates by use case keyword
     *
     * @param request the base request containing requestInfo, deviceInfo and search request
     * @param serviceId the service ID making the request
     * @param moduleId the module ID within the service
     * @param subModuleId the submodule ID
     * @param screenId the screen ID making the request
     * @param channel the channel (WEB, MOBILE, etc.)
     * @param acceptLanguage the accept language preference (en, ar)
     * @return list of notification templates containing the keyword in use case
     */
    @PostMapping("/send-notification/search")
    public ResponseEntity<BaseResponse<List<NotificationTemplateResponseDto>>> getNotificationTemplatesByKeyword(
            @RequestBody BaseRequest request,
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        log.info("Received search request for keyword: {}, serviceId={}, channel={}",
                request.getRequestInfo().getKeyword(), serviceId, channel);

        List<NotificationTemplateResponseDto> templates = configurationService
                .getTemplatesByUseCaseKeyword(request.getRequestInfo().getKeyword());

        log.info("Retrieved {} notification templates for keyword: {}",
                templates.size(), request.getRequestInfo().getKeyword());

        return ResponseBuilder.success(templates, messageSourceService.getMessage(ErrorMessages.TEMPLATES_FOUND_FOR_KEYWORD));
    }

    /**
     * Get notification templates by notification type
     *
     * @param request the base request containing requestInfo, deviceInfo and type request
     * @param serviceId the service ID making the request
     * @param moduleId the module ID within the service
     * @param subModuleId the submodule ID
     * @param screenId the screen ID making the request
     * @param channel the channel (WEB, MOBILE, etc.)
     * @param acceptLanguage the accept language preference (en, ar)
     * @return list of notification templates for the specified type
     */
    @PostMapping("/send-notification/get-by-type")
    public ResponseEntity<BaseResponse<List<NotificationTemplateResponseDto>>> getNotificationTemplatesByType(
            @RequestBody BaseRequest request,
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage) {

        log.info("Received request for notification type: {}, serviceId={}, channel={}",
                request.getRequestInfo().getNotificationType(), serviceId, channel);

        // Convert string to NotificationType enum
        NotificationType type = NotificationType.fromString(request.getRequestInfo().getNotificationType());

        List<NotificationTemplateResponseDto> templates = configurationService
                .getTemplatesByNotificationType(type);

        log.info("Retrieved {} notification templates for type: {}",
                templates.size(), request.getRequestInfo().getNotificationType());

        return ResponseBuilder.success(templates, messageSourceService.getMessage(ErrorMessages.TEMPLATES_FOUND_FOR_TYPE));
    }

}


