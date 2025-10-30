package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardBinValidationWrapper {

    @Valid
    private CardBinValidationRequest requestInfo;
    
    @Valid
    @NotNull(message = "Device information is required")
    private DeviceInfo deviceInfo;
}