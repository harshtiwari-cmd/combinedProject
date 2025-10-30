package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for send notification request
 * 
 * This DTO follows the Single Responsibility Principle by focusing
 * solely on notification request data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

    private String notificationId;

    @NotNull(message = "Parameters are required")
    @NotEmpty(message = "Parameters cannot be empty")
    @Valid
    private List<FieldValueDto> parameters;

    private String correlationId;
    private String language;

}