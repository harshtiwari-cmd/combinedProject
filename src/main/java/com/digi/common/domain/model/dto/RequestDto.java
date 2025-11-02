package com.digi.common.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestDto {

    private RequestInfoDto requestInfoDto;
    private DeviceInfoDto deviceInfo;


    public RequestDto( DeviceInfoDto deviceInfo) {
        this.deviceInfo = deviceInfo;
    }





   }
 