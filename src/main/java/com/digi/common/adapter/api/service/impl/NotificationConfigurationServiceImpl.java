package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.NotificationConfigurationService;
import com.digi.common.adapter.repository.NotificationTemplateRepository;
import com.digi.common.domain.model.NotificationTemplate;
import com.digi.common.domain.model.NotificationType;
import com.digi.common.domain.model.dto.NotificationTemplateResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of NotificationConfigurationService
 * Manages notification template configuration operations
 */
@Service
@AllArgsConstructor
@Slf4j
public class NotificationConfigurationServiceImpl implements NotificationConfigurationService {

    private NotificationTemplateRepository notificationTemplateRepository;

    @Override
    public NotificationTemplateResponseDto getNotificationConfiguration(String notificationId) {
        log.info("Getting notification configuration for ID: {}", notificationId);
        
        // Validate input
        if (!StringUtils.hasText(notificationId)) {
            log.warn("Empty or null notification ID provided");
            throw new IllegalArgumentException("Notification ID cannot be empty or null");
        }
        
        try {
            Long notificationIdLong = Long.parseLong(notificationId.trim());
            
            // Find the notification template by ID
            Optional<NotificationTemplate> templateOptional =
                notificationTemplateRepository.findByNotificationIdAndIsActive(notificationIdLong, 1);
            
            if (templateOptional.isEmpty()) {
                log.warn("Notification template not found or inactive for ID: {}", notificationId);
                throw new IllegalArgumentException("Notification template not found or inactive: " + notificationId);
            }
            
            NotificationTemplate template = templateOptional.get();
            log.info("Found notification template: ID={}, UseCase={}, Type={}", 
                    template.getNotificationId(), template.getUseCase(), template.getNotificationType());
            
            return NotificationTemplateResponseDto.fromEntity(template);
            
        } catch (NumberFormatException e) {
            log.error("Invalid notification ID format: {}", notificationId);
            throw new IllegalArgumentException("Invalid notification ID format: " + notificationId);
        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting configuration for notification ID: {}", notificationId, e);
            throw new RuntimeException("Error retrieving notification configuration", e);
        }
    }
    
    @Override
    public List<NotificationTemplateResponseDto> getAllTemplates() {
        log.info("Retrieving all active notification templates as DTOs");
        try {
            List<NotificationTemplate> templates = notificationTemplateRepository.findByIsActive(1);
            List<NotificationTemplateResponseDto> dtos = templates.stream()
                    .map(NotificationTemplateResponseDto::fromEntity)
                    .toList();
            log.info("Successfully retrieved {} active notification templates as DTOs", dtos.size());
            return dtos;
        } catch (Exception e) {
            log.error("Error retrieving all notification templates as DTOs", e);
            throw new RuntimeException("Error retrieving notification templates as DTOs", e);
        }
    }

    @Override
    public List<NotificationTemplateResponseDto> getTemplatesByUseCaseKeyword(String keyword) {
        log.info("Searching notification templates by use case keyword as DTOs: {}", keyword);
        
        // Validate input
        if (!StringUtils.hasText(keyword)) {
            log.warn("Empty or null keyword provided for template search");
            throw new IllegalArgumentException("Search keyword cannot be empty or null");
        }
        
        try {
            List<NotificationTemplate> templates = notificationTemplateRepository
                    .findByUseCaseContainingIgnoreCaseAndIsActive(keyword.trim());
            List<NotificationTemplateResponseDto> dtos = templates.stream()
                    .map(NotificationTemplateResponseDto::fromEntity)
                    .toList();
            log.info("Successfully retrieved {} notification templates containing keyword '{}' in use case as DTOs", 
                    dtos.size(), keyword);
            return dtos;
        } catch (IllegalArgumentException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving notification templates by keyword as DTOs: {}", keyword, e);
            throw new RuntimeException("Error retrieving notification templates by keyword as DTOs", e);
        }
    }
    
    @Override
    public List<NotificationTemplateResponseDto> getTemplatesByNotificationType(NotificationType type) {
        log.info("Searching notification templates by type as DTOs: {}", type);
        
        // Validate input
        if (type == null) {
            log.warn("Null notification type provided");
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        try {
            List<NotificationTemplate> templates = notificationTemplateRepository
                    .findByNotificationTypeAndIsActive(type);
            List<NotificationTemplateResponseDto> dtos = templates.stream()
                    .map(NotificationTemplateResponseDto::fromEntity)
                    .toList();
            log.info("Successfully retrieved {} notification templates for type: {} as DTOs", dtos.size(), type);
            return dtos;
        } catch (Exception e) {
            log.error("Error retrieving notification templates by type as DTOs: {}", type, e);
            throw new RuntimeException("Error retrieving notification templates by type as DTOs", e);
        }
    }
}
