package com.digi.common.adapter.api.service.impl;


import com.digi.common.adapter.api.service.OtpService;
import com.digi.common.domain.model.OtpConfiguration;
import com.digi.common.domain.model.dto.*;
import com.digi.common.domain.repository.OtpConfigurationRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
public class OtpServiceImpl implements OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OtpConfigurationRepository configRepository;

    @Value("${otp.service.url}")
    private String otpServiceUrl;

    @Override
    public OtpGenerateResponse generateOtp(String unit, String channel, String lang, String serviceId,
                                           String screenId, String moduleId, String subModuleId,
                                           OtpGenerateRequest request) {
        try {
            logger.debug("Calling OTP generation API for customerId: {}",
                    request.getRequestInfo().getCustomerId());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("unit", unit != null ? unit : AppConstant.DEFAULT_UNIT);
            headers.set("channel", channel != null ? channel : AppConstant.DEFAULT_CHANNEL);
            headers.set("accept-language", lang != null ? lang : AppConstant.DEFAULT_LANGUAGE);
            headers.set("serviceId", serviceId != null ? serviceId : AppConstant.DEFAULT_SERVICEID);
            headers.set("screenId", screenId != null ? screenId : AppConstant.DEFAULT_SCREENID);
            headers.set("moduleId", moduleId != null ? moduleId : AppConstant.DEFAULT_MODULEID);
            headers.set("subModuleId", subModuleId != null ? subModuleId : AppConstant.DEFAULT_SUNMODULEID);

            HttpEntity<OtpGenerateRequest> entity = new HttpEntity<>(request, headers);
            ResponseEntity<OtpGenerateResponse> response = restTemplate.exchange(
                    otpServiceUrl,
                    HttpMethod.POST,
                    entity,
                    OtpGenerateResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                OtpGenerateResponse otpResponse = response.getBody();
                logger.info("OTP generation successful for customerId: {}, status: {}",
                        request.getRequestInfo().getCustomerId(),
                        otpResponse.getStatus() != null ? otpResponse.getStatus().getDescription() : "UNKNOWN");
                return otpResponse;
            } else {
                logger.warn("OTP generation failed - HTTP Status: {}, Response: {}",
                        response.getStatusCode(), response.getBody());
                return createFailureResponse("OTP generation failed");
            }

        } catch (Exception e) {
            logger.error("Exception occurred while calling OTP generation API: {}", e.getMessage(), e);
            return createFailureResponse("OTP generation service unavailable");
        }
    }
    @Override
    public GenericResponse<OtpConfigResponseDto> getOtpConfiguration(DefaultHeadersDto headers, RequestDto requestDto) {
        logger.info("Entered getOtpConfiguration, headers={}, requestDto={}", headers, requestDto);
        try {
            Long screenId = Long.valueOf(headers.getScreenId());
            Optional<OtpConfiguration> cfgOpt = configRepository.findByScreenId(screenId);

            if (cfgOpt.isEmpty()) {
                logger.warn("No OTP configuration found for screenId={}", screenId);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.NOT_FOUND_CODE, AppConstant.NOT_FOUND_DESC),
                        null
                );
            }

            OtpConfiguration cfg = cfgOpt.get();

            if (!cfg.isStatus()) {
                logger.warn("OTP configuration found but disabled for screenId={}", screenId);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.GEN_ERROR_CODE, "OTP configuration is disabled for this screen"),
                        null
                );
            }

            OtpConfigResponseDto dto = new OtpConfigResponseDto(
                    cfg.getOtpLength(),
                    cfg.getOtpExpirySeconds(),
                    cfg.getOtpMaxAttempts()
            );

            logger.info("Returning OTP configuration response: {}", dto);
            return new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC), dto);

        } catch (Exception e) {
            logger.error("Error in getOtpConfiguration", e);
            return new GenericResponse<>(new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC), null);
        }
    }
    private OtpGenerateResponse createFailureResponse(String message) {
        return OtpGenerateResponse.builder()
                .status(OtpGenerateResponse.Status.builder()
                        .code(AppConstant.GEN_ERROR_CODE)
                        .description(AppConstant.GEN_ERROR_DESC)
                        .build())
                .data(OtpGenerateResponse.OtpData.builder()
                        .message(message)
                        .build())
                .build();
    }
}
