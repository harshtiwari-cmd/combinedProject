package com.digi.common.domain.model.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class ActivateCardValidateWrapper {

    @Valid
    private ActivateCardRequest requestInfo;

    @Valid
    private DeviceInfo deviceInfo;

}
