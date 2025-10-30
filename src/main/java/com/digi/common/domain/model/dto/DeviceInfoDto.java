package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoDto {
    private String deviceId;
    private String ipAddress;
    private String vendorId;
    private String osVersion;
    private String osType;
    private String appVersion;
    private String endToEndId;

   }