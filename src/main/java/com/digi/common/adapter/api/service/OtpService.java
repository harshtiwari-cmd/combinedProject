package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.*;
import com.digi.common.dto.GenericResponse;

public interface OtpService {


    OtpGenerateResponse generateOtp(String unit, String channel, String lang, String serviceId,
                                    String screenId, String moduleId, String subModuleId,
                                    OtpGenerateRequest request);
    GenericResponse<OtpConfigResponseDto> getOtpConfiguration(DefaultHeadersDto headers, RequestDto requestDto);
}
