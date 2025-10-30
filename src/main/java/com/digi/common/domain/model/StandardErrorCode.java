package com.digi.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error codes used across all APIs
 * Following organizational standards for error code format
 */
@Getter
@AllArgsConstructor
public enum StandardErrorCode {
    
    SUCCESS("000000", "Success"),
    BAD_REQUEST("000400", "Bad request"),
    DEVICE_INFO_NOT_FOUND("000400", "Device info not found"),
    MANDATORY_HEADER_NOT_FOUND("000400", "Mandatory header not found"),
    NO_DATA_FOUND("000404", "No Data Found"),
    REQUEST_TIMEOUT("000408", "Request Timeout"),
    DUPLICATE_REQUEST("000409", "Duplicate request"),
    INTERNAL_SERVER_ERROR("000500", "Internal server error"),
    SERVICE_UNAVAILABLE("000503", "Service Unavailable");
    
    private final String code;
    private final String description;
    
    /**
     * Get error code from HTTP status code
     */
    public static StandardErrorCode fromHttpStatus(int httpStatus) {
        return switch (httpStatus) {
            case 200 -> SUCCESS;
            case 400 -> BAD_REQUEST;
            case 404 -> NO_DATA_FOUND;
            case 408 -> REQUEST_TIMEOUT;
            case 409 -> DUPLICATE_REQUEST;
            case 500 -> INTERNAL_SERVER_ERROR;
            case 503 -> SERVICE_UNAVAILABLE;
            default -> INTERNAL_SERVER_ERROR;
        };
    }
}
