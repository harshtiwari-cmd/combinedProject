package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.NotificationType;
import com.digi.common.domain.model.dto.NotificationTemplateResponseDto;

import java.util.List;

/**
 * Service interface for managing notification configurations
 */
public interface NotificationConfigurationService {
    
    /**
     * Get notification configuration by notification ID
     * 
     * @param notificationId the notification ID
     * @return the notification template response DTO
     */
    NotificationTemplateResponseDto getNotificationConfiguration(String notificationId);
    
    /**
     * Get all active notification templates
     * 
     * @return list of all active notification templates
     */
    List<NotificationTemplateResponseDto> getAllTemplates();
    
    /**
     * Get notification templates by use case keyword
     * 
     * @param keyword the keyword to search in use case
     * @return list of matching notification templates
     */
    List<NotificationTemplateResponseDto> getTemplatesByUseCaseKeyword(String keyword);
    
    /**
     * Get notification templates by notification type
     * 
     * @param type the notification type
     * @return list of matching notification templates
     */
    List<NotificationTemplateResponseDto> getTemplatesByNotificationType(NotificationType type);
}
