package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.ActivateCardService;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.ActivateCardRequest;
import com.digi.common.domain.model.dto.ActivateCardResponse;
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
public class MockActivateCardServiceImpl implements ActivateCardService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GenericResponse<ActivateCardResponse> createNewPin(
            String unit, String chanel, String lang,
            String serviceId, String screenId, String moduleId,
            String subModuleId, ActivateCardRequest request,
            DeviceInfo deviceInfo) {

        log.info("[MOCK] createNewPin called with cardNumber={}, unit={}, channel={}, lang={}",
                request.getCardNumber(), unit, chanel, lang);

        try {
            String cardNumber = request.getCardNumber();
            String mockResponseFile = getMockResponseFile(cardNumber);

            log.debug("[MOCK] Loading mock response from file: {}", mockResponseFile);

            ClassPathResource resource = new ClassPathResource(mockResponseFile);

            GenericResponse<ActivateCardResponse> mockResponse = objectMapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<>() {}
            );

            log.info("[MOCK] Successfully loaded mock response for cardNumber={}", cardNumber);
            return mockResponse;

        } catch (IOException e) {
            log.error("[MOCK] IOException while reading mock file for cardNumber={}: {}", request.getCardNumber(), e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);

        } catch (Exception e) {
            log.error("[MOCK] Unexpected error during mock processing for cardNumber={}: {}", request.getCardNumber(), e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }

    private String getMockResponseFile(String cardNumber) {
        switch (cardNumber) {
            case "5271581234123456":
                log.info("[MOCK] Matched cardNumber={} to success mock file", cardNumber);
                return "JSON/card-activation-success.json";

            case "3209741234567889":
                log.info("[MOCK] Matched cardNumber={} to failure mock file", cardNumber);
                return "JSON/card-activation-failed.json";

            case "3459741234567889":
                log.info("[MOCK] Matched cardNumber={} to failure mock file", cardNumber);
                return "JSON/pin-creation-failed.json";

            default:
                log.warn("[MOCK] No specific mock file found for cardNumber={}, using default success", cardNumber);
                return "JSON/card-activation-success.json";
        }
    }
}