package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.CardStatusService;
import com.digi.common.domain.model.dto.CardStatusResponse;
import com.digi.common.domain.model.dto.CardStatusValidationRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "true")
public class MockCardStatusServiceImpl implements CardStatusService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GenericResponse<CardStatusResponse> validateCardStatus(
            String unit,
            String channel,
            String acceptLanguage,
            String serviceId,
            String screenId,
            String moduleId,
            String subModuleId,
            CardStatusValidationRequest request,
            DeviceInfo deviceInfo) {

        log.info("[MOCK] validateCardStatus called | unit={}, channel={}, serviceId={}, cardNumber={}",
                unit, channel, serviceId, request != null ? request.getCardNumber() : null);

        try {
            if (request == null || request.getCardNumber() == null || request.getCardNumber().isEmpty()) {
                log.warn("[MOCK] BIN pre-check failed: no record found for cardNumber");
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.BIN_VALIDATE_DATA_MSG);
            }

            String cardNumber = request.getCardNumber();
            String mockResponseFile = getMockResponseFile(cardNumber);

            log.info("[MOCK] Loading mock response from file: {}", mockResponseFile);

            ClassPathResource resource = new ClassPathResource(mockResponseFile);

            GenericResponse<CardStatusResponse> mockResponse = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {
                    }
            );

            log.info("[MOCK] Mock response loaded successfully for cardNumber={}", cardNumber);
            return mockResponse;

        } catch (IOException e) {
            log.error("[MOCK] Error loading mock response: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);

        } catch (Exception e) {
            log.error("[MOCK] Unexpected error while processing card status mock: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }


    private String getMockResponseFile(String cardNumber) {
        switch (cardNumber) {
            case "4203741234567889":
                return "JSON/card-status-success.json";

            case "3209741234567889":
                return "JSON/card-status-failed.json";

            default:
                log.warn("[MOCK] No specific mock file found for cardNumber={}, using default success", cardNumber);
                return "JSON/card-status-success.json";
        }
    }
}
