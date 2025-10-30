package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseRequest {
    
    private RequestInfo requestInfo;
    private DeviceInfo deviceInfo;
}


