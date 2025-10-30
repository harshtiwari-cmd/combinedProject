package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.OtpConfiguration;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.OtpConfigResponseDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.repository.OtpConfigurationRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OtpServiceImplTest {

    @Mock
    private OtpConfigurationRepository configRepository;

    @InjectMocks
    private OtpServiceImpl otpService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ 1️⃣ Happy Path - Found and Enabled
    @Test
    void testGetOtpConfiguration_FoundEnabled() {
        Long screenId = 1L;
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", screenId.toString(), "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        OtpConfiguration config = new OtpConfiguration();
        config.setScreenId(screenId);
        config.setOtpLength(6);
        config.setOtpExpirySeconds(120);
        config.setOtpMaxAttempts(3);
        config.setStatus(true);

        when(configRepository.findByScreenId(screenId)).thenReturn(Optional.of(config));

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(AppConstant.RESULT_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.RESULT_DESC, response.getStatus().getDescription());

        OtpConfigResponseDto dto = response.getData();
        assertEquals(6, dto.getOtpLength());
        assertEquals(120, dto.getOtpExpiryTime());
        assertEquals(3, dto.getOtpRetryCount());

        verify(configRepository, times(1)).findByScreenId(screenId);
    }

    // ✅ 2️⃣ Config Found but Disabled
    @Test
    void testGetOtpConfiguration_FoundDisabled() {
        Long screenId = 2L;
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", screenId.toString(), "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        OtpConfiguration config = new OtpConfiguration();
        config.setScreenId(screenId);
        config.setOtpLength(6);
        config.setOtpExpirySeconds(100);
        config.setOtpMaxAttempts(2);
        config.setStatus(false);

        when(configRepository.findByScreenId(screenId)).thenReturn(Optional.of(config));

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals("OTP configuration is disabled for this screen", response.getStatus().getDescription());

        verify(configRepository, times(1)).findByScreenId(screenId);
    }

    // ✅ 3️⃣ Config Not Found
    @Test
    void testGetOtpConfiguration_NotFound() {
        Long screenId = 3L;
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", screenId.toString(), "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        when(configRepository.findByScreenId(screenId)).thenReturn(Optional.empty());

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals(AppConstant.NOT_FOUND_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.NOT_FOUND_DESC, response.getStatus().getDescription());

        verify(configRepository, times(1)).findByScreenId(screenId);
    }

    // ✅ 4️⃣ Invalid Screen ID (NumberFormatException)
    @Test
    void testGetOtpConfiguration_InvalidScreenId() {
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", "invalid", "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verifyNoInteractions(configRepository);
    }

    // ✅ 5️⃣ Repository Throws Exception
    @Test
    void testGetOtpConfiguration_RepositoryThrowsException() {
        Long screenId = 5L;
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", screenId.toString(), "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        when(configRepository.findByScreenId(screenId)).thenThrow(new RuntimeException("DB error"));

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNull(response.getData());
        assertEquals(AppConstant.GEN_ERROR_CODE, response.getStatus().getCode());
        assertEquals(AppConstant.GEN_ERROR_DESC, response.getStatus().getDescription());

        verify(configRepository, times(1)).findByScreenId(screenId);
    }

    // ✅ 6️⃣ Found Config With Zero Values
    @Test
    void testGetOtpConfiguration_ZeroValues() {
        Long screenId = 6L;
        DefaultHeadersDto headers = new DefaultHeadersDto(
                "service", "module", "subModule", screenId.toString(), "channel", "en"
        );
        RequestDto requestDto = new RequestDto();

        OtpConfiguration config = new OtpConfiguration();
        config.setScreenId(screenId);
        config.setOtpLength(0);
        config.setOtpExpirySeconds(0);
        config.setOtpMaxAttempts(0);
        config.setStatus(true);

        when(configRepository.findByScreenId(screenId)).thenReturn(Optional.of(config));

        GenericResponse<OtpConfigResponseDto> response = otpService.getOtpConfiguration(headers, requestDto);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(0, response.getData().getOtpLength());
        assertEquals(0, response.getData().getOtpExpiryTime());
        assertEquals(0, response.getData().getOtpRetryCount());

        verify(configRepository, times(1)).findByScreenId(screenId);
    }
}
 