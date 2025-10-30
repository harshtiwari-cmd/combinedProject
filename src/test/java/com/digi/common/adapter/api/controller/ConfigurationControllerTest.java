package com.digi.common.adapter.api.controller;

import com.digi.common.dto.ApiResponse;
import com.digi.common.dto.ConfigurationDto;
import com.digi.common.dto.BaseServiceRequest;
import com.digi.common.adapter.api.controller.ConfigurationController;
import com.digi.common.adapter.api.service.ConfigurationService;
import com.digi.common.dto.DeviceInfoDto;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private ConfigurationController controller;

    @BeforeEach
    void setUp() {
        // Setup will be done per test as needed
    }

    @Test
    void testGetCallbackFields_Success() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("customerName");
        config.setFieldLength("50");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithNullScreenName() {
        // Given
        String screenName = null;
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("defaultField");
        config.setFieldLength("10");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithEmptyScreenName() {
        // Given
        String screenName = "";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("emptyField");
        config.setFieldLength("5");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithDifferentLanguages() {
        // Given
        String screenName = "MULTI_LANG_SCREEN";
        String lang = "ar"; // Arabic
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("arabicField");
        config.setFieldLength("100");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "ar",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithFrenchLanguage() {
        // Given
        String screenName = "FRENCH_SCREEN";
        String lang = "fr"; // French
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("frenchField");
        config.setFieldLength("75");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "fr",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithSpanishLanguage() {
        // Given
        String screenName = "SPANISH_SCREEN";
        String lang = "es"; // Spanish
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("spanishField");
        config.setFieldLength("60");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "es",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithEmptyLanguage() {
        // Given
        String screenName = "EMPTY_LANG_SCREEN";
        String lang = ""; // Empty language
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("emptyLangField");
        config.setFieldLength("30");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithNullLanguage() {
        // Given
        String screenName = "NULL_LANG_SCREEN";
        String lang = null; // Null language
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("nullLangField");
        config.setFieldLength("25");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                null,
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithSpecialCharactersInScreenName() {
        // Given
        String screenName = "SCREEN@#$%^&*()";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("specialCharField");
        config.setFieldLength("40");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithLongScreenName() {
        // Given
        String screenName = "VERYLONGSCREENNAMETHATEXCEEDSNORMALLENGTHLIMITS123456789";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("longScreenField");
        config.setFieldLength("200");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithNumericScreenName() {
        // Given
        String screenName = "123456789";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("numericScreenField");
        config.setFieldLength("15");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithMixedCaseScreenName() {
        // Given
        String screenName = "MiXeDcAsEsCrEeN";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("mixedCaseField");
        config.setFieldLength("35");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("DEVICE123");
        deviceInfo.setDeviceName("Test Device");
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithNullDeviceInfo() {
        // Given
        String screenName = "NULL_DEVICE_SCREEN";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("nullDeviceField");
        config.setFieldLength("20");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of(), null);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }

    @Test
    void testGetCallbackFields_WithComplexDeviceInfo() {
        // Given
        String screenName = "COMPLEX_DEVICE_SCREEN";
        String lang = "en";
        
        ConfigurationDto config = new ConfigurationDto();
        config.setFieldName("complexDeviceField");
        config.setFieldLength("80");
        config.setFieldType("text");
        config.setSequence(1);
        
        List<ConfigurationDto> mockConfigurations = Arrays.asList(config);
        ApiResponse<ConfigurationDto> expectedResponse = ApiResponse.success(mockConfigurations);

        when(configurationService.getRequestCallbackFields(screenName, lang))
                .thenReturn(expectedResponse);

        // When
        DeviceInfoDto deviceInfo = new DeviceInfoDto();
        deviceInfo.setDeviceId("COMPLEX_DEVICE_123");
        deviceInfo.setDeviceName("Complex Test Device");
        deviceInfo.setIpAddress("192.168.1.100");
        // deviceInfo.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        // deviceInfo.setPlatform("Windows");
        
        BaseServiceRequest baseServiceRequest = new BaseServiceRequest(Map.of("additionalData", "test"), deviceInfo);
        
        ResponseEntity<ApiResponse<ConfigurationDto>> response = controller.getCallbackFields(
                "WEB",
                "en",
                "SERVICE001",
                screenName,
                "MODULE001",
                "SUB001",
                baseServiceRequest
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(configurationService, times(1)).getRequestCallbackFields(screenName, lang);
    }
}
