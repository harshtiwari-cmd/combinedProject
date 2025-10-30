package com.digi.common.domain.model.dto;

import com.digi.common.domain.model.NotificationTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateResponseDto {
    
    private Long notificationId;
    private List<String> parameterList;
    private String useCase;
    private String notificationType;
    private Boolean isActive;
    
    // Static method to convert from entity
    public static NotificationTemplateResponseDto fromEntity(NotificationTemplate template) {
        return NotificationTemplateResponseDto.builder()
                .notificationId(template.getNotificationId())
                .parameterList(convertToList(template.getParameterList()))
                .useCase(template.getUseCase())
                .notificationType(template.getNotificationType() != null ? template.getNotificationType().getValue() : null)
                .isActive(template.isActive()) // Use helper method that returns boolean
                .build();
    }

    private static List<String> convertToList(String commaSeperatedString) {
        if (commaSeperatedString == null || commaSeperatedString.trim().isEmpty()) {
            return new ArrayList<>(); // return empty list instead of null
        }

        return Arrays.stream(commaSeperatedString.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
