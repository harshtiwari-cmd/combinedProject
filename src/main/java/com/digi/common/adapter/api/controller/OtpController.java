package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.OtpService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.HeaderDeviceConstant;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private  OtpService otpService;

      // Get OTP configuration
      @PostMapping("/configuration")
      public GenericResponse<OtpConfigResponseDto> getOtpConfiguration(
              @RequestHeader(name = AppConstants.SERVICE_ID, required = false) String serviceId,
              @RequestHeader(name = AppConstants.MODULE_ID, required = false) String moduleId,
              @RequestHeader(name = AppConstants.SUB_MODULE_ID, required = false) String subModuleId,
              @RequestHeader(name = AppConstants.SCREENID, required = false) String screenId,
              @RequestHeader(name = AppConstants.CHANNEL, required = false) String channel,
              @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, defaultValue = "en", required = false) String acceptLanguage,
              @RequestBody RequestDto requestDto
      ) {
          // Validate headers
          List<String> missingHeaders = HeaderDeviceConstant.missingMandatoryHeaders(serviceId, moduleId, subModuleId, screenId, channel);
          if (!missingHeaders.isEmpty()) {
              return new GenericResponse<>(
                      new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.MANDATORY_HEADERS_DESC),
                      null
              );
          }

          // Validate device info
          if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
              return new GenericResponse<>(
                      new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC),
                      null
              );
          }
          String languageCode = HeaderDeviceConstant.mapLanguage(acceptLanguage);


          // Construct headers and call service
          DefaultHeadersDto headers = new DefaultHeadersDto(
                  serviceId, moduleId, subModuleId, screenId, channel, languageCode
          );

          return otpService.getOtpConfiguration(headers, requestDto);
      }
}