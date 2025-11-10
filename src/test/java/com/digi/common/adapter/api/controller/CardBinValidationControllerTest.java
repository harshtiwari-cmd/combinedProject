package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.CardBinValidationService;
import com.digi.common.adapter.api.service.CardStatusService;
import com.digi.common.domain.model.dto.*;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.persistance.CardBinMaster;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBinValidationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardBinValidationService cardBinValidationService;

    @Mock
    private CardStatusService cardStatusService;

    @InjectMocks
    private CardBinValidationController cardBinValidationController;

    @Autowired
    private ObjectMapper objectMapper;

    private CardBinValidationWrapper wrapper;
    private CardBinValidationRequest request;
    private SimpleValidationResponse validationResponse;
    private DeviceInfo deviceInfo;

    private CardStatusValidateWrapper cardStatusValidateWrapper;

    @BeforeEach
    void setUp() {
        deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.0.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        request = CardBinValidationRequest.builder()
                .cardNumber("1234567890123456")
                .pin("1234")
                .build();

        wrapper = CardBinValidationWrapper.builder()
                .requestInfo(request)
                .deviceInfo(deviceInfo)
                .build();

        validationResponse = SimpleValidationResponse.builder()
                .customerId("123456")
                .userName("testuser")
                .otpStatus(true)
                .build();

        CardStatusValidationRequest validationRequest = CardStatusValidationRequest.builder().cardNumber("4203741234567889").build();

       cardStatusValidateWrapper = CardStatusValidateWrapper.builder()
                .requestInfo(validationRequest)
                .deviceInfo(deviceInfo)
                .build();

    }

    @Test
    void testValidateCardBin_Success() throws Exception {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.success(validationResponse);

        when(cardBinValidationService.validateCardBin(
                eq("test-unit"), eq("web"), eq("en"), eq("service123"),
                eq("screen123"), eq("module123"), eq("submodule123"),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> genericResponseResponseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = genericResponseResponseEntity.getBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(AppConstant.RESULT_CODE, actualResponse.getStatus().getCode());
        assertNotNull(actualResponse.getData());

        SimpleValidationResponse data = actualResponse.getData();
        assertEquals("123456", data.getCustomerId());
        assertEquals("testuser", data.getUserName());
        assertTrue(data.isOtpStatus());

        verify(cardBinValidationService, times(1)).validateCardBin(
                eq("test-unit"), eq("web"), eq("en"), eq("service123"),
                eq("screen123"), eq("module123"), eq("submodule123"),
                any(CardBinValidationRequest.class), any(DeviceInfo.class));
    }

    @Test
    void testValidateCardBin_ServiceReturnsError() throws Exception {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.error(
                AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC);

        when(cardBinValidationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> responseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = responseEntity.getBody();

        // Then
        assertNotNull(actualResponse);
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());
        assertNull(actualResponse.getData(), "Expected data to be null on validation failure");

        verify(cardBinValidationService, times(1)).validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)
        );
    }

    @Test
    void testValidateCardBin_ServiceReturnsNull() throws Exception {
        // Given
        when(cardBinValidationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenReturn(null);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> responseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = responseEntity.getBody();

        // Then
        assertNotNull(actualResponse, "Response should not be null even if service returned null");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());
        assertNull(actualResponse.getData(), "Expected data to be null when service returns null");

        verify(cardBinValidationService, times(1)).validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)
        );
    }

    @Test
    void testValidateCardBin_ServiceReturnsNullStatus() throws Exception {
        // Given
        GenericResponse<SimpleValidationResponse> serviceResponse = new GenericResponse<>();
        serviceResponse.setStatus(null);

        when(cardBinValidationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> responseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = responseEntity.getBody();

        // Then
        assertNotNull(actualResponse, "Response body should not be null");
        assertNotNull(actualResponse.getStatus(), "Controller should provide a default failure status when service status is null");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());

        verify(cardBinValidationService, times(1)).validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)
        );
    }

    @Test
    void testValidateCardBin_ServiceReturnsInvalidData() throws Exception {
        // Given
        SimpleValidationResponse invalidResponse = SimpleValidationResponse.builder()
                .customerId(null)
                .userName(null)
                .otpStatus(false)
                .build();

        GenericResponse<SimpleValidationResponse> serviceResponse = GenericResponse.success(invalidResponse);

        when(cardBinValidationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenReturn(serviceResponse);

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> responseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = responseEntity.getBody();

        // Then
        assertNotNull(actualResponse, "Response should not be null even if data is invalid");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());

        // The controller should not treat invalid data as valid
        SimpleValidationResponse data = actualResponse.getData();
        if (data != null) {
            assertNull(data.getCustomerId(), "Customer ID should remain null in invalid response");
            assertNull(data.getUserName(), "Username should remain null in invalid response");
            assertFalse(data.isOtpStatus(), "OTP status should be false for invalid data");
        }

        verify(cardBinValidationService, times(1)).validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)
        );
    }

    @Test
    void testValidateCardBin_ServiceThrowsException() throws Exception {
        // Given
        when(cardBinValidationService.validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When
        ResponseEntity<GenericResponse<SimpleValidationResponse>> responseEntity = cardBinValidationController.validateCardBin(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                "submodule123",
                wrapper
        );
        GenericResponse<SimpleValidationResponse> actualResponse = responseEntity.getBody();

        // Then
        assertNotNull(actualResponse, "Response should not be null even when service throws exception");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());
        assertNull(actualResponse.getData(), "Data should be null when service throws exception");

        verify(cardBinValidationService, times(1)).validateCardBin(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(),
                any(CardBinValidationRequest.class), any(DeviceInfo.class)
        );
    }

    @Test
    void testGetActiveBins_Success() throws Exception {
        // Given
        CardBinMaster bin1 = CardBinMaster.builder()
                .code("CODE1")
                .bin("123456")
                .productType("CREDIT")
                .cardType("VISA")
                .status("ACTIVE")
                .build();

        CardBinMaster bin2 = CardBinMaster.builder()
                .code("CODE2")
                .bin("654321")
                .productType("DEBIT")
                .cardType("MASTERCARD")
                .status("ACTIVE")
                .build();

        List<CardBinMaster> activeBins = List.of(bin1, bin2);
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.success(activeBins);

        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(deviceInfo)
                .build();

        // When

        GenericResponse<List<CardBinMaster>> actualResponse = cardBinValidationController.getActiveBins(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",

                wrapper
        );



        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.RESULT_CODE, actualResponse.getStatus().getCode());
        assertNotNull(actualResponse.getData(), "Response data should not be null");

        List<CardBinMaster> data = actualResponse.getData();
        assertEquals(2, data.size(), "There should be two active bins");

        assertEquals("123456", data.get(0).getBin());
        assertEquals("654321", data.get(1).getBin());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_NoData() throws Exception {
        // Given
        GenericResponse<List<CardBinMaster>> serviceResponse =
                GenericResponse.success(Collections.emptyList());
        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(deviceInfo)
                .build();

        // When
        GenericResponse<List<CardBinMaster>> actualResponse = cardBinValidationController.getActiveBins(
                "test-unit",
                "web",
                "en",
                "service123",
                "screen123",
                "module123",
                wrapper
        );

        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.NO_DATA_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.NODATA, actualResponse.getStatus().getDescription());
        assertTrue(actualResponse.getData().isEmpty(), "Expected no active bins in the response");

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_NullWrapper() throws Exception {
        // Given
        CardBinAllWrapper nullWrapper = null;

        // When
        GenericResponse<List<CardBinMaster>> actualResponse =
                cardBinValidationController.getActiveBins(
                        "test-unit",
                        "web",
                        "en",
                        "service123",
                        "screen123",
                        "module123",
                        nullWrapper
                );

        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.GEN_ERROR_CODE, actualResponse.getStatus().getCode());
        assertEquals("Request body is required", actualResponse.getStatus().getDescription());

        // No service call expected
        verify(cardBinValidationService, times(0)).getActiveBins();
    }

    @Test
    void testGetActiveBins_NullDeviceInfo() {
        // Given
        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(null)
                .build();

        // When
        GenericResponse<List<CardBinMaster>> actualResponse =
                cardBinValidationController.getActiveBins(
                        "test-unit",
                        "web",
                        "en",
                        "service123",
                        "screen123",
                        "module123",
                        wrapper
                );

        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.GEN_ERROR_CODE, actualResponse.getStatus().getCode());
        assertEquals("Device information is required", actualResponse.getStatus().getDescription());

        // Service should not be called
        verify(cardBinValidationService, times(0)).getActiveBins();
    }

    @Test
    void testGetActiveBins_ServiceReturnsError() {
        // Given
        GenericResponse<List<CardBinMaster>> serviceResponse = GenericResponse.error(
                AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC);
        when(cardBinValidationService.getActiveBins()).thenReturn(serviceResponse);

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(deviceInfo)
                .build();

        // When
        GenericResponse<List<CardBinMaster>> actualResponse =
                cardBinValidationController.getActiveBins(
                        "test-unit",
                        "web",
                        "en",
                        "service123",
                        "screen123",
                        "module123",
                        wrapper
                );

        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testGetActiveBins_ServiceThrowsException() {
        // Given
        when(cardBinValidationService.getActiveBins())
                .thenThrow(new RuntimeException("Database error"));

        CardBinAllWrapper wrapper = CardBinAllWrapper.builder()
                .deviceInfo(deviceInfo)
                .build();

        // When
        GenericResponse<List<CardBinMaster>> actualResponse =
                cardBinValidationController.getActiveBins(
                        "test-unit",
                        "web",
                        "en",
                        "service123",
                        "screen123",
                        "module123",
                        wrapper
                );

        // Then
        assertNotNull(actualResponse, "Response should not be null");
        assertEquals(AppConstant.VALIDATION_FAILURE_CODE, actualResponse.getStatus().getCode());
        assertEquals(AppConstant.VALIDATION_FAILURE_DESC, actualResponse.getStatus().getDescription());

        verify(cardBinValidationService, times(1)).getActiveBins();
    }

    @Test
    void testvalidateCardStatus_returnSuccess() {

        CardStatusResponse statusResponse = CardStatusResponse.builder()
                .cardValid(true)
                .activationFlag("Active")
                .build();

        GenericResponse<CardStatusResponse> genericResponse = GenericResponse.success(statusResponse);

        when(cardStatusService.validateCardStatus(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any(CardStatusValidationRequest.class), any(DeviceInfo.class)
        )).thenReturn(genericResponse);

        ResponseEntity<GenericResponse<CardStatusResponse>> validatedCardStatus = cardBinValidationController.validateCardStatus(
                "unit", "channel", "en", "service", "screen", "moduleId", "subModuleId", cardStatusValidateWrapper
        );

        GenericResponse<CardStatusResponse> cardStatusBody = validatedCardStatus.getBody();

        assertEquals(AppConstant.RESULT_CODE, cardStatusBody.getStatus().getCode());
        assertEquals(AppConstant.SUCCESS, cardStatusBody.getStatus().getDescription());
        assertEquals("Active", cardStatusBody.getData().getActivationFlag());
        assertTrue(cardStatusBody.getData().isCardValid());

    }

    @Test
    void testvalidateCardStatus_throwException() {

        when(cardStatusService.validateCardStatus(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(),
                anyString(), any(CardStatusValidationRequest.class), any(DeviceInfo.class)
        )).thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<GenericResponse<CardStatusResponse>> validatedCardStatus = cardBinValidationController.validateCardStatus(
                "unit", "channel", "en", "service", "screen", "moduleId", "subModuleId", cardStatusValidateWrapper
        );

        GenericResponse<CardStatusResponse> cardStatusBody = validatedCardStatus.getBody();

        assertEquals(AppConstant.GEN_ERROR_CODE, cardStatusBody.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, cardStatusBody.getStatus().getDescription());
        assertNull(cardStatusBody.getData());

    }




}