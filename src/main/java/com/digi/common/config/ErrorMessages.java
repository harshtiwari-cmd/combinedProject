package com.digi.common.config;

/**
 * Centralized constants for internationalized message keys.
 * All actual messages are stored in messages.properties and messages_ar.properties
 * 
 * <p>This class provides type-safe access to message keys used with constants.
 * Using constants instead of string literals prevents typos and makes refactoring easier.</p>
 * 
 * <p>Example usage:
 * <pre>
 * String message = messageSourceService.getMessage(ErrorMessages.CONFIGURATION_RETRIEVED);
 * </pre>
 * </p>
 * 
 */
public final class ErrorMessages {
    
    // Prevent instantiation
    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    // Generic message keys
    public static final String INTERNAL_SERVER_ERROR = "error.internal.server";
    public static final String SERVICE_UNAVAILABLE = "error.service.unavailable";
    public static final String BAD_REQUEST = "error.bad.request";
    public static final String INVALID_REQUEST_PARAMETERS = "error.invalid.request.parameters";
    
    // Configuration-related message keys
    public static final String CONFIGURATION_RETRIEVED = "success.configuration.retrieved";
    public static final String ALL_TEMPLATES_RETRIEVED = "success.templates.all.retrieved";
    public static final String TEMPLATES_FOUND_FOR_KEYWORD = "success.templates.found.keyword";
    public static final String TEMPLATES_FOUND_FOR_TYPE = "success.templates.found.type";
    public static final String CONFIGURATION_NOT_FOUND = "error.configuration.not.found";
    public static final String INVALID_NOTIFICATION_ID = "error.configuration.invalid.id";
    public static final String INVALID_KEYWORD = "error.configuration.invalid.keyword";
    public static final String INVALID_NOTIFICATION_TYPE = "error.configuration.invalid.type";
    
    // Notification-related message keys
    public static final String SMS_PROCESSED_SUCCESSFULLY = "success.sms.processed";
    public static final String NOTIFICATION_SENT = "success.notification.sent";
    public static final String NOTIFICATION_FAILED = "error.notification.failed";
    
    // Validation message keys
    public static final String MISSING_NOTIFICATION_ID = "error.validation.missing.id";
    public static final String MISSING_PARAMETERS = "error.validation.missing.parameters";
    public static final String INVALID_LANGUAGE = "error.validation.invalid.language";
    public static final String MISSING_MOBILE_NUMBER = "error.validation.missing.mobile";
    
    // Middleware-related message keys
    public static final String MIDDLEWARE_ERROR = "error.middleware.error";
    public static final String MIDDLEWARE_UNAVAILABLE = "error.middleware.unavailable";
    public static final String MIDDLEWARE_TIMEOUT = "error.middleware.timeout";
    public static final String MIDDLEWARE_CONNECTION_ERROR = "error.middleware.connection";
    public static final String MIDDLEWARE_NULL_RESPONSE = "error.middleware.null.response";
}



