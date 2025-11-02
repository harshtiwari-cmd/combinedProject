package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.OtpService;
import com.digi.common.domain.model.dto.DeviceInfoDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RequestInfoDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;

class OtpControllerTest {

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    private RequestDto validRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        DeviceInfoDto deviceInfo = new DeviceInfoDto(
                "DEVICE123", "192.168.1.10", "VENDOR001",
                "14.4.2", "Android", "1.0.5", "END2END-98765"
        );

        RequestInfoDto requestInfo = new RequestInfoDto();
        validRequest = new RequestDto();
        validRequest.setDeviceInfo(deviceInfo);
        validRequest.setRequestInfoDto(requestInfo);
    }

    // ✅ Test 1: Missing mandatory headers → BAD_REQUEST
    @Test
    void testMissingHeaders_ShouldReturnBadRequest() {
        GenericResponse<OtpConfigResponseDto> response = otpController.getOtpConfiguration(
                null, null, null, null, null, "en", validRequest
        );

        assertNotNull(response);
        assertEquals(AppConstant.BAD_REQUEST_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.MANDATORY_HEADERS_DESC, response.getStatus().getDescription());
        assertNull(response.getData());
        verifyNoInteractions(otpService);
    }

    // ✅ Test 2: Missing device info → BAD_REQUEST
    @Test
    void testMissingDeviceInfo_ShouldReturnBadRequest() {
        RequestDto invalidRequest = new RequestDto(); // Missing device info

        GenericResponse<OtpConfigResponseDto> response = otpController.getOtpConfiguration(
                "SVC1", "MOD1", "SUB1", "SCR1", "WEB", "en", invalidRequest
        );

        assertNotNull(response);
        assertEquals(AppConstant.BAD_REQUEST_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.DEVICE_INFO_DESC, response.getStatus().getDescription());
        assertNull(response.getData());
        verifyNoInteractions(otpService);
    }

    // ✅ Test 3: Valid request → SUCCESS
    @Test
    void testValidRequest_ShouldReturnSuccess() {
        OtpConfigResponseDto config = new OtpConfigResponseDto();
        GenericResponse<OtpConfigResponseDto> serviceResponse =
                new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC), config);

        when(otpService.getOtpConfiguration(any(), any())).thenReturn(serviceResponse);

        GenericResponse<OtpConfigResponseDto> response = otpController.getOtpConfiguration(
                "SVC1", "MOD1", "SUB1", "SCR1", "MOBILE", "en", validRequest
        );

        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getStatus().getDescription());
        assertNotNull(response.getData());
        verify(otpService, times(1)).getOtpConfiguration(any(), any());
    }

    // ✅ Test 4: Valid request (Arabic language) → SUCCESS
    @Test
    void testValidRequest_WithDifferentLanguage_ShouldReturnSuccess() {
        OtpConfigResponseDto config = new OtpConfigResponseDto();
        GenericResponse<OtpConfigResponseDto> serviceResponse =
                new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, "Success"), config);

        when(otpService.getOtpConfiguration(any(), any())).thenReturn(serviceResponse);

        GenericResponse<OtpConfigResponseDto> response = otpController.getOtpConfiguration(
                "SVC2", "MOD2", "SUB2", "SCR2", "APP", "ar", validRequest
        );

        assertNotNull(response);
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals("Success", response.getStatus().getDescription());
        assertNotNull(response.getData());
        verify(otpService, times(1)).getOtpConfiguration(any(), any());
    }

}
 