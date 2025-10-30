package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for middleware service request
 * Matches the MqRequestDto format from dkn-middleware-service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiddlewareRequestDto {
    private String serviceName;
    private List<FieldValueDto> parameters;
    private String correlationId;
    private String language;
}