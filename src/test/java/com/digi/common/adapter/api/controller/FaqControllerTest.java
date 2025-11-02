package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.FaqService;
import com.digi.common.domain.model.dto.*;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.HeaderDeviceConstant;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


class FaqControllerTest {

    @Mock
    private FaqService faqService;

    @InjectMocks
    private FaqController faqController;

    private RequestDto validRequest;
    private MockedStatic<HeaderDeviceConstant> headerDeviceMock;

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

        // Start static mock for HeaderDeviceConstant
        headerDeviceMock = mockStatic(HeaderDeviceConstant.class);
    }

    @AfterEach
    void tearDown() {
        headerDeviceMock.close();
    }

    // ✅ Test 1: Missing headers should return BAD_REQUEST
    @Test
    void testMissingHeaders_ShouldReturnBadRequest() {
        headerDeviceMock.when(() -> HeaderDeviceConstant.missingMandatoryHeaders(any(), any(), any(), any(), any()))
                .thenReturn(List.of("serviceId"));

        ResponseEntity<GenericResponse<FaqResponse>> response = faqController.getFaqs(
                null, null, null, null, null, "en", validRequest
        );

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(AppConstant.BAD_REQUEST_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.MANDATORY_HEADERS_DESC, response.getBody().getStatus().getDescription());
        verifyNoInteractions(faqService);
    }

    // ✅ Test 2: Missing device info should return BAD_REQUEST
    @Test
    void testMissingDeviceInfo_ShouldReturnBadRequest() {
        RequestDto invalidRequest = new RequestDto(); // missing device info

        headerDeviceMock.when(() -> HeaderDeviceConstant.missingMandatoryHeaders(any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        headerDeviceMock.when(() -> HeaderDeviceConstant.hasValidDeviceInfo(any()))
                .thenReturn(false);

        ResponseEntity<GenericResponse<FaqResponse>> response = faqController.getFaqs(
                "SVC1", "MOD1", "SUB1", "SCR1", "WEB", "en", invalidRequest
        );

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(AppConstant.BAD_REQUEST_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.DEVICE_INFO_DESC, response.getBody().getStatus().getDescription());
        verifyNoInteractions(faqService);
    }

    // ✅ Test 3: Valid request should return SUCCESS
    @Test
    void testValidRequest_ShouldReturnSuccess() {
        FaqResponse faqResponse = new FaqResponse(List.of(new FaqDTO("Q1", "A1")));
        GenericResponse<FaqResponse> serviceResponse =
                new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC), faqResponse);

        headerDeviceMock.when(() -> HeaderDeviceConstant.missingMandatoryHeaders(any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        headerDeviceMock.when(() -> HeaderDeviceConstant.hasValidDeviceInfo(any()))
                .thenReturn(true);
        headerDeviceMock.when(() -> HeaderDeviceConstant.mapLanguage(anyString()))
                .thenReturn("en");

        when(faqService.getFaqs(any(), any())).thenReturn(serviceResponse);

        ResponseEntity<GenericResponse<FaqResponse>> response = faqController.getFaqs(
                "SVC1", "MOD1", "SUB1", "SCR1", "WEB", "en", validRequest
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(AppConstant.RESULT_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getBody().getStatus().getDescription());
        assertNotNull(response.getBody().getData());
        verify(faqService, times(1)).getFaqs(any(), any());
    }

    // ✅ Test 4: Valid request with different language should still succeed
    @Test
    void testValidRequest_WithDifferentLanguage_ShouldReturnSuccess() {
        FaqResponse faqResponse = new FaqResponse(List.of(new FaqDTO("Q2", "A2")));
        GenericResponse<FaqResponse> serviceResponse =
                new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC), faqResponse);

        headerDeviceMock.when(() -> HeaderDeviceConstant.missingMandatoryHeaders(any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        headerDeviceMock.when(() -> HeaderDeviceConstant.hasValidDeviceInfo(any()))
                .thenReturn(true);
        headerDeviceMock.when(() -> HeaderDeviceConstant.mapLanguage("ar"))
                .thenReturn("ar");

        when(faqService.getFaqs(any(), any())).thenReturn(serviceResponse);

        ResponseEntity<GenericResponse<FaqResponse>> response = faqController.getFaqs(
                "SVC2", "MOD2", "SUB2", "SCR2", "APP", "ar", validRequest
        );

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(AppConstant.RESULT_CODE, response.getBody().getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getBody().getStatus().getDescription());
        assertNotNull(response.getBody().getData());
        verify(faqService, times(1)).getFaqs(any(), any());
    }

}
 