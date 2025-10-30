package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.OtpService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OtpControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(otpController).build();
    }

    @Test
    void testGetOtpConfiguration_Success() throws Exception {
        // Arrange mock data
        OtpConfigResponseDto otpConfig = new OtpConfigResponseDto(6, 120, 3); // ✅ Correct order
        GenericResponse<OtpConfigResponseDto> mockResponse = new GenericResponse<>();
        mockResponse.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
        mockResponse.setData(otpConfig);

        when(otpService.getOtpConfiguration(any(DefaultHeadersDto.class), any(RequestDto.class)))
                .thenReturn(mockResponse);

        RequestDto requestDto = new RequestDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Act & Assert
        mockMvc.perform(post("/otp/configuration")
                        .header(AppConstants.SERVICE_ID, "SERVICE_001")
                        .header(AppConstants.MODULE_ID, "MODULE_01")
                        .header(AppConstants.SUB_MODULE_ID, "SUB_MODULE_01")
                        .header(AppConstants.SCREENID, "SCREEN_01")
                        .header(AppConstants.CHANNEL, "MOBILE")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstants.RESULT_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstants.RESULT_DESC))
                .andExpect(jsonPath("$.data.otpLength").value(6))          // ✅ first param
                .andExpect(jsonPath("$.data.otpExpiryTime").value(120))    // ✅ second param
                .andExpect(jsonPath("$.data.otpRetryCount").value(3));     // ✅ third param
    }


    @Test
    void testGetOtpConfiguration_MissingRequiredHeader() throws Exception {
        RequestDto requestDto = new RequestDto();
        String requestBody = objectMapper.writeValueAsString(requestDto);

        // Missing SERVICE_ID -> should return 400 (Spring fails before controller call)
        mockMvc.perform(post("/otp/configuration")
                        .header(AppConstants.MODULE_ID, "MODULE_01")
                        .header(AppConstants.SUB_MODULE_ID, "SUB_MODULE_01")
                        .header(AppConstants.SCREENID, "SCREEN_01")
                        .header(AppConstants.CHANNEL, "MOBILE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOtpConfiguration_EmptyRequestBody() throws Exception {
        // Arrange mock data
        OtpConfigResponseDto otpConfig = new OtpConfigResponseDto(120, 3, 6);
        GenericResponse<OtpConfigResponseDto> mockResponse = new GenericResponse<>();
        mockResponse.setStatus(new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC));
        mockResponse.setData(otpConfig);

        // Mock service with any() for exact types
        when(otpService.getOtpConfiguration(any(DefaultHeadersDto.class), any(RequestDto.class)))
                .thenReturn(mockResponse);

        String emptyRequestBody = "{}";

        // Act & Assert
        mockMvc.perform(post("/otp/configuration")
                        .header(AppConstants.SERVICE_ID, "SERVICE_001")
                        .header(AppConstants.MODULE_ID, "MODULE_01")
                        .header(AppConstants.SUB_MODULE_ID, "SUB_MODULE_01")
                        .header(AppConstants.SCREENID, "SCREEN_01")
                        .header(AppConstants.CHANNEL, "MOBILE")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstants.RESULT_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstants.RESULT_DESC))
                .andExpect(jsonPath("$.data.otpExpiryTime").value(otpConfig.getOtpExpiryTime()))
                .andExpect(jsonPath("$.data.otpRetryCount").value(otpConfig.getOtpRetryCount()))
                .andExpect(jsonPath("$.data.otpLength").value(otpConfig.getOtpLength()));
    }

}
 