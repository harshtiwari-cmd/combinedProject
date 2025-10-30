package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestInfo {
    
    // API-specific fields based on the configuration endpoint
    private String notificationId;
    private String keyword;
    private String notificationType;
    
    // For NotificationController - parameters array
    private java.util.List<FieldValueDto> parameters;
}
