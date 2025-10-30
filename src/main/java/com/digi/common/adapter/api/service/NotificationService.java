package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.NotificationRequestDto;
import com.digi.common.domain.model.dto.SendNotificationResponseDto;

/**
 * Service interface for notification operations
 * 
 * This interface follows the Interface Segregation Principle by providing
 * only notification sending operations.
 */
public interface NotificationService {

    /**
     * Send a notification based on the provided request
     * 
     * @param request the notification request containing use case, channel, and parameters
     * @return response containing the result of the notification sending operation
     */
    SendNotificationResponseDto sendNotification(NotificationRequestDto request);

}