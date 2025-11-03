package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.BankMiddlewareService;
import com.digi.common.domain.model.dto.*;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.helper.CardBasicValidations;
import com.digi.common.infrastructure.persistance.CardBinMaster;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CardStatusServiceImplTest {

    @Mock
    private CardBasicValidations cardBasicValidations;

    @Mock
    private BankMiddlewareService bankMiddlewareService;

    @InjectMocks
    private CardStatusServiceImpl cardStatusService;

    private DeviceInfo deviceInfo;
    private CardStatusValidationRequest request;
    private BankMiddlewareResponse bankResponse;

    @BeforeEach
    void setUp() {

        bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .message("Success")
                .bankResponse(BankMiddlewareResponse.BankResponse.builder()
                        .customerNumber("2233300011")
                        .correlationId("CORR123")
                        .returnStatus(BankMiddlewareResponse.ReturnStatus.builder()
                                .returnCode("0000")
                                .returnCodeDesc("SUCCESS")
                                .build())
                        .build())
                .build();


        deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

       request = CardStatusValidationRequest.builder().cardNumber("4203741234567889").build();
    }

    @Test
    public void testValidateCard_serviceReturnSuccess() {

        CardBinMaster cardBin1 = CardBinMaster.builder()
                .code("CBM001")
                .bin("12345678901234567890")
                .productType("Credit Card")
                .cardType("DEBIT")
                .status("ACTIVE")
                .build();


        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBin1);

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);

        assertEquals(AppConstant.RESULT_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.SUCCESS, genericResponse.getStatus().getDescription());
        assertNotNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));

    }

    @Test
    public void testValidateCard_serviceReturnBinMasterNull() {

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(null);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);

        assertEquals(AppConstant.ERROR_DATA_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.BIN_VALIDATE_DATA_MSG, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());

    }

    @Test
    public void testValidateCard_serviceReturnInactiveBin() {

        CardBinMaster cardBin1 = CardBinMaster.builder()
                .code("CBM001")
                .bin("12345678901234567890")
                .productType("Credit Card")
                .cardType("DEBIT")
                .status("INACTIVE")
                .build();


        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBin1);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);


        assertEquals(AppConstant.ERROR_DATA_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.BIN_VALIDATE_DATA_MSG, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());

    }

    @Test
    public void testValidateCard_serviceReturnInvalidCardType() {

        CardBinMaster cardBin1 = CardBinMaster.builder()
                .code("CBM001")
                .bin("12345678901234567890")
                .productType("ISLAMIC PLATINUM")
                .cardType("MASTER")
                .status("ACTIVE")
                .build();

        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBin1);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);

        assertEquals(AppConstant.ERROR_DATA_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.BIN_VALIDATE_DATA_MSG, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());

    }

    @Test
    public void testValidateCard_serviceUnavailable() {

        bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .message("Success")
                .bankResponse(BankMiddlewareResponse.BankResponse.builder()
                        .customerNumber("2233300011")
                        .correlationId("CORR123")
                        .returnStatus(BankMiddlewareResponse.ReturnStatus.builder()
                                .returnCode("000424")
                                .returnCodeDesc("SUCCESS")
                                .build())
                        .build())
                .build();

        CardBinMaster cardBin1 = CardBinMaster.builder()
                .code("CBM001")
                .bin("12345678901234567890")
                .productType("Credit Card")
                .cardType("DEBIT")
                .status("ACTIVE")
                .build();


        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBin1);

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus(null, null, null, null, null, null, null, request, deviceInfo);

        assertEquals(AppConstant.INNER_SERVICE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.INNER_SERVICE_MSG, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));

    }

    @Test
    public void testValidateCard_serviceReturnGeneralException() {

        bankResponse = BankMiddlewareResponse.builder()
                .status("SUCCESS")
                .message("Success")
                .bankResponse(BankMiddlewareResponse.BankResponse.builder()
                        .customerNumber("2233300011")
                        .correlationId("CORR123")
                        .returnStatus(BankMiddlewareResponse.ReturnStatus.builder()
                                .returnCode("000121") // Invalid Status code
                                .returnCodeDesc("SUCCESS")
                                .build())
                        .build())
                .build();

        CardBinMaster cardBin1 = CardBinMaster.builder()
                .code("CBM001")
                .bin("12345678901234567890")
                .productType("Credit Card")
                .cardType("DEBIT")
                .status("ACTIVE")
                .build();


        when(cardBasicValidations.findMatchingBin(anyString())).thenReturn(cardBin1);

        when(bankMiddlewareService.callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class)))
                .thenReturn(bankResponse);

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);

        assertEquals(AppConstant.GEN_ERROR_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());
        verify(bankMiddlewareService, times(1)).callBankMiddleware(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), any(BankMiddlewareRequest.class));

    }

    @Test
    public void testValidateCard_serviceThrowException() {

        when(cardBasicValidations.findMatchingBin(anyString())).thenThrow(new RuntimeException("Database Error"));

        GenericResponse<CardStatusResponse> genericResponse = cardStatusService.validateCardStatus("unit", "channel", "en", "service", "screen", "module", "subModule", request, deviceInfo);

        assertEquals(AppConstant.GEN_ERROR_CODE, genericResponse.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, genericResponse.getStatus().getDescription());
        assertNull(genericResponse.getData());

        verify(cardBasicValidations, times(1)).findMatchingBin(anyString());
    }


}