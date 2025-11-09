package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.BankMiddlewareService;
import com.digi.common.adapter.api.service.ActivateCardService;
import com.digi.common.domain.model.dto.*;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.exception.BARWAHSMEncryptionException;
import com.digi.common.exception.BARWAHSMParsingException;
import com.digi.common.exception.BarwaHSMCommuicationException;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.hsm.HSMEncryptorManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(name = "mock.enabled", havingValue = "false")
public class ActivateCardServiceImpl implements ActivateCardService {

    @Autowired
    private BankMiddlewareService bankMiddlewareService;

    @Autowired
    private HSMEncryptorManagerImpl hsmEncryptor;

    @Override
    public GenericResponse<ActivateCardResponse> createNewPin(String unit, String chanel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, ActivateCardRequest request, DeviceInfo deviceInfo) {

        log.info("Starting createNewPin process for customerNumber: {}", request.getCustomerNumber());

        try {
            String cardNumber = request.getCardNumber();
            String customerNumber = request.getCustomerNumber();


            String newPinBlock = request.getNewPinBlock();


            log.debug("Received request with cardNumber: {}, customerNumber: {}", cardNumber, customerNumber);

            String encryptedPin;
            try {
                encryptedPin = hsmEncryptor.generatePinBlockUnderZPK(newPinBlock, cardNumber, "PINCREATION");
                log.info("PIN encryption successful for card: {}", cardNumber);
            } catch (BarwaHSMCommuicationException | BARWAHSMEncryptionException | BARWAHSMParsingException e) {
                log.error("HSM encryption failed for card: {}, error: {}", cardNumber, e.getMessage(), e);
                return GenericResponse.error(AppConstant.ERROR_DATA_CODE, AppConstant.PIN_ENCRYPT_DATA_MSG);
            }

            BankMiddlewareRequest middlewareRequest = BankMiddlewareRequest.builder()
                    .serviceName(AppConstant.PIN_CREATION_SERVICE)
                    .parameters(Arrays.asList(

                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName(AppConstant.CUSTOMER_NO)
                                    .fieldValue(customerNumber)
                                    .build(),
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName(AppConstant.CARD_NO)
                                    .fieldValue(cardNumber)
                                    .build(),
                            BankMiddlewareRequest.Parameter.builder()
                                    .fieldName(AppConstant.NEW_PIN)
                                    .fieldValue(encryptedPin)
                                    .build()
                    ))
                    .build();

            log.info("Calling BankMiddlewareService with request: {}", middlewareRequest);

            BankMiddlewareResponse bankMiddlewareResponse = bankMiddlewareService.callBankMiddleware(
                    unit != null ? unit : AppConstant.DEFAULT_UNIT,
                    chanel != null ? chanel : AppConstant.DEFAULT_CHANNEL,
                    lang != null ? lang : AppConstant.DEFAULT_LANGUAGE,
                    serviceId != null ? serviceId : AppConstant.SERVICE_ID,
                    screenId != null ? screenId : AppConstant.SCREEN_ID,
                    moduleId != null ? moduleId : AppConstant.MODULE_ID,
                    subModuleId != null ? subModuleId : AppConstant.SUB_MODULE_ID,
                    middlewareRequest
            );

            log.debug("Received response from BankMiddlewareService: {}", bankMiddlewareResponse);

            String returnCode = Optional.ofNullable(bankMiddlewareResponse)
                    .map(BankMiddlewareResponse::getBankResponse)
                    .map(BankMiddlewareResponse.BankResponse::getReturnStatus)
                    .map(BankMiddlewareResponse.ReturnStatus::getReturnCode)
                    .orElse(null);

            log.info("BankMiddleware return code: {}", returnCode);

            if (AppConstant.MIDDLEWARE_SUCCESS_CODE.equalsIgnoreCase(returnCode)) {

                log.info("PIN creation successful for customerNumber: {}", customerNumber);

                log.debug("Building middlewareRequest-2 for card activation");

                BankMiddlewareRequest middlewareRequest2 = BankMiddlewareRequest.builder()
                        .serviceName(AppConstant.CARD_ACTIVATION_SERVICE)
                        .parameters(Arrays.asList(
                                BankMiddlewareRequest.Parameter.builder()
                                        .fieldName(AppConstant.CARD_NO)
                                        .fieldValue(cardNumber)
                                        .build()
                        ))
                        .build();

                log.info("Calling BankMiddlewareService for card activation with request: {}", middlewareRequest2);

                BankMiddlewareResponse bankMiddlewareResponse2 = bankMiddlewareService.callBankMiddleware(
                        unit != null ? unit : AppConstant.DEFAULT_UNIT,
                        chanel != null ? chanel : AppConstant.DEFAULT_CHANNEL,
                        lang != null ? lang : AppConstant.DEFAULT_LANGUAGE,
                        serviceId != null ? serviceId : AppConstant.SERVICE_ID,
                        screenId != null ? screenId : AppConstant.SCREEN_ID,
                        moduleId != null ? moduleId : AppConstant.MODULE_ID,
                        subModuleId != null ? subModuleId : AppConstant.SUB_MODULE_ID,
                        middlewareRequest2
                );

                log.debug("Received response from BankMiddlewareService for card activation: {}", bankMiddlewareResponse2);

                String returnCode2 = Optional.ofNullable(bankMiddlewareResponse2)
                        .map(BankMiddlewareResponse::getBankResponse)
                        .map(BankMiddlewareResponse.BankResponse::getReturnStatus)
                        .map(BankMiddlewareResponse.ReturnStatus::getReturnCode)
                        .orElse(null);

                log.info("BankMiddleware return code for card activation: {}", returnCode2);

                if (AppConstant.MIDDLEWARE_SUCCESS_CODE.equalsIgnoreCase(returnCode2)) {
                    log.info("Card activation successful for cardNumber: {}", cardNumber);
                    ActivateCardResponse newPinResponse = ActivateCardResponse.builder().cardActive("true").build();
                    return GenericResponse.success(newPinResponse);
                }

                if (AppConstant.MIDDLEWARE_FAILURE_CODE.equalsIgnoreCase(returnCode2)) {
                    log.warn("Card activation failed for cardNumber: {}", cardNumber);

                    ResultUtilVO resultUtilVO = new ResultUtilVO();
                    resultUtilVO.setCode("0004");
                    resultUtilVO.setDescription("CARD ACTIVATION FAILED");
                    return new GenericResponse<>(null, resultUtilVO);
                }

                log.warn("Unhandled return code from card activation: {} for cardNumber: {}", returnCode2, cardNumber);

            }

            if (AppConstant.MIDDLEWARE_FAILURE_CODE.equalsIgnoreCase(returnCode)) {
                log.warn("PIN creation failed with return code 0004 for customerNumber: {}", customerNumber);

                ResultUtilVO resultUtilVO = new ResultUtilVO();
                resultUtilVO.setCode("0004");
                resultUtilVO.setDescription("PIN CREATION FAILED");
                return new GenericResponse<>(null, resultUtilVO);
            }

            log.error("Unhandled return code: {} for customerNumber: {}", returnCode, customerNumber);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        } catch (Exception e) {
            log.error("Exception occurred during createNewPin: {}", e.getMessage(), e);
            return GenericResponse.error(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC);
        }
    }
}