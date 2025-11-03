package com.digi.common.adapter.api.service.impl;

import com.digi.common.dto.ResultUtilVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.digi.common.domain.model.dto.CardStatusResponse;
import com.digi.common.domain.model.dto.CardStatusValidationRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MockCardStatusServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MockCardStatusServiceImpl mockService;


    @Test
    void testValidateCardStatus_SuccessResponse() throws IOException {

        CardStatusValidationRequest request = new CardStatusValidationRequest();

        request.setCardNumber("4203741234567889");
        DeviceInfo deviceInfo = new DeviceInfo();

        CardStatusResponse statusResponse = CardStatusResponse.builder()
                .cardValid(true)
                .activationFlag("Active")
                .build();

        GenericResponse<CardStatusResponse> responseGenericResponse = GenericResponse.success(statusResponse);

        when(objectMapper.readValue(
                (InputStream) any(InputStream.class),
                ArgumentMatchers.<TypeReference<GenericResponse<CardStatusResponse>>>any()
        )).thenReturn(responseGenericResponse);

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);


        assertNotNull(response.getStatus());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertTrue(response.getData().isCardValid());
        assertEquals("Active", response.getData().getActivationFlag());
    }

    @Test
    void testValidateCardStatus_DefaultSuccessResponse() throws IOException {

        CardStatusValidationRequest request = new CardStatusValidationRequest();

        request.setCardNumber("3333331234567889");
        DeviceInfo deviceInfo = new DeviceInfo();

        CardStatusResponse statusResponse = CardStatusResponse.builder()
                .cardValid(true)
                .activationFlag("Active")
                .build();

        GenericResponse<CardStatusResponse> responseGenericResponse = GenericResponse.success(statusResponse);

        when(objectMapper.readValue(
                (InputStream) any(InputStream.class),
                ArgumentMatchers.<TypeReference<GenericResponse<CardStatusResponse>>>any()
        )).thenReturn(responseGenericResponse);

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);


        assertNotNull(response.getStatus());
        assertEquals("000000", response.getStatus().getCode());
        assertEquals("SUCCESS", response.getStatus().getDescription());
        assertNotNull(response.getData());
        assertTrue(response.getData().isCardValid());
        assertEquals("Active", response.getData().getActivationFlag());
    }

    @Test
    void testValidateCardStatus_FailedResponse() throws IOException {

        CardStatusValidationRequest request = new CardStatusValidationRequest();

        request.setCardNumber("3209741234567889");
        DeviceInfo deviceInfo = new DeviceInfo();


        GenericResponse<CardStatusResponse> genericResponse = new GenericResponse<>();

        ResultUtilVO status = new ResultUtilVO();
        status.setCode("000424");
        status.setDescription("Card does not exist");

        genericResponse.setStatus(status);
        genericResponse.setData(null);


        when(objectMapper.readValue(
                (InputStream) any(InputStream.class),
                ArgumentMatchers.<TypeReference<GenericResponse<CardStatusResponse>>>any()
        )).thenReturn(genericResponse);

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);


        assertNotNull(response.getStatus());
        assertEquals("000424", response.getStatus().getCode());
        assertEquals("Card does not exist", response.getStatus().getDescription());


    }

    @Test
    void testValidateCardStatus_NullCardNumber() {

        CardStatusValidationRequest request = new CardStatusValidationRequest();
        request.setCardNumber(null);
        DeviceInfo deviceInfo = new DeviceInfo();

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);

        assertNotNull(response);
        assertEquals(AppConstant.ERROR_DATA_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.BIN_VALIDATE_DATA_MSG, response.getStatus().getDescription());
    }

    @Test
    void testMockJsonFilesExist() throws Exception {
        assertTrue(new ClassPathResource("JSON/card-status-success.json").exists(),
                "Missing success.json in resources!");

        assertTrue(new ClassPathResource("JSON/card-status-failed.json").exists(),
                "Missing failed.json in resources!");
    }

    @Test
    void testValidateCardStatus_returnIoException() throws IOException {

        CardStatusValidationRequest request = new CardStatusValidationRequest();

        request.setCardNumber("4203741234567889");
        DeviceInfo deviceInfo = new DeviceInfo();



        when(objectMapper.readValue(
                (InputStream) any(InputStream.class),
                ArgumentMatchers.<TypeReference<GenericResponse<CardStatusResponse>>>any()
        )).thenThrow(new IOException("IO Exception"));

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);

        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());
        assertNull( response.getData());

    }

    @Test
    void testValidateCardStatus_returnException() throws IOException {

        CardStatusValidationRequest request = new CardStatusValidationRequest();

        request.setCardNumber("4203741234567889");
        DeviceInfo deviceInfo = new DeviceInfo();


        when(objectMapper.readValue(
                (InputStream) any(InputStream.class),
                ArgumentMatchers.<TypeReference<GenericResponse<CardStatusResponse>>>any()
        )).thenThrow(new RuntimeException("Database Error"));

        GenericResponse<CardStatusResponse> response = mockService.validateCardStatus(
                "UNIT1", "CHANNEL1", "en", "SERVICE1", "SCREEN1", "MODULE1", "SUBMODULE1",
                request, deviceInfo);

        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());
        assertNull( response.getData());

    }
}
