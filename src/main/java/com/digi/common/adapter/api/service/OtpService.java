package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;

import com.digi.common.domain.model.dto.OtpGenerateRequest;
import com.digi.common.domain.model.dto.OtpGenerateResponse;

public interface OtpService {


    OtpGenerateResponse generateOtp(String unit, String channel, String lang, String serviceId,
                                    String screenId, String moduleId, String subModuleId,
                                    OtpGenerateRequest request);
    GenericResponse<OtpConfigResponseDto> getOtpConfiguration(DefaultHeadersDto headers, RequestDto requestDto);
}
