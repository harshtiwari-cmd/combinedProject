package com.digi.common.adapter.api.controller;


import com.digi.common.adapter.api.service.CardBinValidationService;
import com.digi.common.adapter.api.service.CardStatusService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.*;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.domain.model.dto.CardStatusValidateWrapper;
import com.digi.common.domain.model.dto.CardStatusValidationRequest;
import com.digi.common.domain.model.dto.CardStatusResponse;
import com.digi.common.infrastructure.persistance.CardBinMaster;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;

@RestController
@Validated
public class CardBinValidationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CardBinValidationController.class);
    @Autowired
    private CardBinValidationService cardBinValidationService;
    @Autowired
    private CardStatusService cardStatusService;
    @Value("${mock.enabled}")
    private boolean isTrue;


    @PostMapping("/validate")
    public ResponseEntity<GenericResponse<SimpleValidationResponse>> validateCardBin(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstants.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID, required = true) String subModuleId,
            @Valid @RequestBody CardBinValidationWrapper wrapper) {

           CardBinValidationRequest request = wrapper.getRequestInfo();
           DeviceInfo deviceRequest = wrapper.getDeviceInfo();
           logger.info("CardBin validation and PIN encryption request received - Unit: {}, Channel: {}, ServiceId: {}, CardNumber: {}",
                unit, channel, serviceId, maskCardNumber(request.getCardNumber()));

        try {
            GenericResponse<SimpleValidationResponse> response = cardBinValidationService.validateCardBin(
                    unit, channel, lang, serviceId, screenId, moduleId, subModuleId, request, deviceRequest);

            if (isTrue) {
                return ResponseEntity.ok(response);
            }

            if (response == null || response.getStatus() == null) {
                logger.warn("Service returned null response or status");
                return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
            }

            if (AppConstant.RESULT_CODE.equals(response.getStatus().getCode())) {
                SimpleValidationResponse data = response.getData();
                if (data == null || (data.getCustomerId() == null && data.getUserName() == null && !data.isOtpStatus())) {
                    logger.warn("Success response but invalid data structure");
                    return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
                }
                return ResponseEntity.ok(GenericResponse.success(data));
            }

            logger.warn("Service returned error response - Code: {}, Description: {}",
                    response.getStatus().getCode(), response.getStatus().getDescription());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred during CardBin validation and PIN encryption - Unit: {}, Channel: {}, ServiceId: {}, Error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return ResponseEntity.ok(GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC));
        }
    }

    @PostMapping("/api/v1/card-validate")
    public ResponseEntity<GenericResponse<CardStatusResponse>> validateCardStatus(
            @RequestHeader(name = AppConstant.HEADER_UNIT, required = true) String unit,
            @RequestHeader(name = AppConstant.HEADER_CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, required = false) String lang,
            @RequestHeader(name = AppConstant.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @Valid @RequestBody CardStatusValidateWrapper wrapper) {

        CardStatusValidationRequest request = wrapper.getRequestInfo();
        DeviceInfo deviceRequest = wrapper.getDeviceInfo();
        logger.info("Card status validation request received - Unit: {}, Channel: {}, ServiceId: {}, CardNumber: {}",
                unit, channel, serviceId, maskCardNumber(request.getCardNumber()));

        try {
            GenericResponse<CardStatusResponse> response = cardStatusService.validateCardStatus(
                    unit, channel, lang, serviceId, screenId, moduleId, subModuleId, request, deviceRequest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error in card status validation - Unit: {}, Channel: {}, ServiceId: {}, Error: {}",
                    unit, channel, serviceId, e.getMessage(), e);
            return ResponseEntity.ok(GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC));
        }
    }
    
    @PostMapping("/bin-details")
    public GenericResponse<List<CardBinMaster>> getActiveBins(
            @RequestHeader(name = AppConstant.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestHeader(name = AppConstant.HEADER_CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, required = false) String lang,
            @Valid @RequestBody(required = true) CardBinAllWrapper wrapper) {
        
        if (wrapper == null) {
            logger.error("Request body is null - ServiceId: {}, ModuleId: {}", serviceId, moduleId);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Request body is required");
        }
        
        if (wrapper.getDeviceInfo() == null) {
            logger.error("Device information is null - ServiceId: {}, ModuleId: {}", serviceId, moduleId);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, "Device information is required");
        }
        
        logger.info("Request received to fetch active CardBin records - ServiceId: {}, ModuleId: {}, SubModuleId: {}, ScreenId: {}, Channel: {}, DeviceId: {}", 
                serviceId, moduleId, subModuleId, screenId, channel, wrapper.getDeviceInfo().getDeviceId());
        try {
            GenericResponse<List<CardBinMaster>> response = cardBinValidationService.getActiveBins();
            if (response == null || response.getStatus() == null || !AppConstant.RESULT_CODE.equals(response.getStatus().getCode())) {
                logger.error("Service returned error response for getActiveBins");
                return GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC);
            }
            
            if (response.getData() == null || response.getData().isEmpty()) {
                logger.info("No active CardBin records found");
                return GenericResponse.successNoData(Collections.emptyList());
            }
            logger.info("Fetched active CardBin records - count: {}", response.getData().size());
            return GenericResponse.success(response.getData());
        } catch (Exception e) {
            logger.error("Error occurred while fetching active CardBin records: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC);
        }
    }

    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return "****";
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
    
}
