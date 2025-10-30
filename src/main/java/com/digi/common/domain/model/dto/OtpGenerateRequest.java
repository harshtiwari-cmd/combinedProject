package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpGenerateRequest {
    
    @NotNull(message = "Request info is required")
    @Valid
    private RequestInfo requestInfo;
    
    @NotNull(message = "Device info is required")
    @Valid
    private DeviceInfo deviceInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestInfo {
        @NotBlank(message = "Action is required")
        private String action;
        
        @NotBlank(message = "Customer number is required")
        private String customerId;
    }
}
