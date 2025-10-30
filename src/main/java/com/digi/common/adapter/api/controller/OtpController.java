package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.OtpService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.domain.model.dto.RequestDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private  OtpService otpService;

      // Get OTP configuration
    @PostMapping("/configuration")
    public GenericResponse<OtpConfigResponseDto> getOtpConfiguration(
            @RequestHeader(name = AppConstants.SERVICE_ID) String serviceId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name=AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE,defaultValue = "en", required = false) String acceptLanguage,
            @RequestBody RequestDto requestDto)
    {

        DefaultHeadersDto headers = new DefaultHeadersDto(
                serviceId, moduleId, subModuleId, screenId, channel, acceptLanguage
        );
        return otpService.getOtpConfiguration(headers,requestDto);

    }
}