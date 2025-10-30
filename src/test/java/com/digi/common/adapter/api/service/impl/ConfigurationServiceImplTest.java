package com.digi.common.adapter.api.service.impl;

import com.digi.common.dto.ApiResponse;
import com.digi.common.dto.ConfigurationDto;
import com.digi.common.exception.BusinessException;
import com.digi.common.entity.Configuration;
import com.digi.common.entity.ConfigurationFieldOptions;
import com.digi.common.entity.ScreenDetails;
import com.digi.common.repository.ConfigurationRepository;
import com.digi.common.repository.ConfigurationFieldOptionsRepository;
import com.digi.common.repository.ScreenDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfigurationServiceImplTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @Mock
    private ConfigurationFieldOptionsRepository configurationFieldOptionsRepository;

    @Mock
    private ScreenDetailsRepository screenDetailsRepository;

    @InjectMocks
    private ConfigurationServiceImpl configurationService;

    private ScreenDetails screenDetails;
    private Configuration configuration;
    private ConfigurationFieldOptions fieldOption;

    @BeforeEach
    void setUp() {
        // Setup ScreenDetails
        screenDetails = new ScreenDetails();
        screenDetails.setScreenId(1L);
        screenDetails.setScreenName("TEST_SCREEN");
        screenDetails.setIsActive(true);

        // Setup Configuration
        configuration = new Configuration();
        configuration.setId(1L);
        configuration.setFieldKey("testField");
        configuration.setEnglishFieldName("Test Field");
        configuration.setArabicFieldName("حقل الاختبار");
        configuration.setFieldOption("M");
        configuration.setFieldLength("50");
        configuration.setFieldValidations("required");
        configuration.setFieldType("combo");
        configuration.setSequence(1);
        configuration.setErrMessage("Custom error message for test field");
        configuration.setRequiresProfanityCheck(true);

        // Setup ConfigurationFieldOptions
        fieldOption = new ConfigurationFieldOptions();
        fieldOption.setId(1L);
        fieldOption.setConfigurationId(1L);
        fieldOption.setEnglishOptionValue("Option1");
        fieldOption.setArabicOptionValue("خيار1");
        fieldOption.setIsActive(true);
    }

    @Test
    void testGetRequestCallbackFields_Success_English() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Arrays.asList(fieldOption));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("testField", dto.getFieldKey());
        assertEquals("Test Field", dto.getFieldName());
        assertEquals("M", dto.getFieldOption());
        assertEquals("50", dto.getFieldLength());
        assertEquals("required", dto.getFieldValidations());
        assertEquals("combo", dto.getFieldType());
        assertEquals(1, dto.getSequence());
        assertEquals("Custom error message for test field", dto.getErrMessage());
        assertEquals(true, dto.getRequiresProfanityCheck());
        assertNotNull(dto.getFieldList());
        assertEquals(1, dto.getFieldList().size());
        assertEquals("Option1", dto.getFieldList().get(0));

        verify(screenDetailsRepository).findByScreenNameIgnoreCaseAndIsActive(screenName, true);
        verify(configurationRepository).findByScreenIdAndIsActiveOrderBySequence(1L, true);
    }

    @Test
    void testGetRequestCallbackFields_Success_Arabic() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "AR";
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Arrays.asList(fieldOption));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("testField", dto.getFieldKey());
        assertEquals("حقل الاختبار", dto.getFieldName());
        assertEquals("Custom error message for test field", dto.getErrMessage());
        assertEquals(true, dto.getRequiresProfanityCheck());
        assertEquals("خيار1", dto.getFieldList().get(0));

        verify(screenDetailsRepository).findByScreenNameIgnoreCaseAndIsActive(screenName, true);
        verify(configurationRepository).findByScreenIdAndIsActiveOrderBySequence(1L, true);
    }

    @Test
    void testGetRequestCallbackFields_ScreenNotFound() {
        // Given
        String screenName = "NON_EXISTENT_SCREEN";
        String lang = "EN";
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields(screenName, lang));
        
        assertEquals("Screen not found: NON_EXISTENT_SCREEN", exception.getMessage());
        assertEquals("SCREEN_NOT_FOUND", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_NoConfigurations() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Collections.emptyList());

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void testGetRequestCallbackFields_NullScreenName() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields(null, "EN"));
        
        assertEquals("Screen name is required", exception.getMessage());
        assertEquals("INVALID_SCREEN_NAME", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_EmptyScreenName() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields("", "EN"));
        
        assertEquals("Screen name is required", exception.getMessage());
        assertEquals("INVALID_SCREEN_NAME", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_WhitespaceScreenName() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields("   ", "EN"));
        
        assertEquals("Screen name is required", exception.getMessage());
        assertEquals("INVALID_SCREEN_NAME", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_NullLanguage() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields("TEST_SCREEN", null));
        
        assertEquals("Language is required", exception.getMessage());
        assertEquals("INVALID_LANGUAGE", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_EmptyLanguage() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields("TEST_SCREEN", ""));
        
        assertEquals("Language is required", exception.getMessage());
        assertEquals("INVALID_LANGUAGE", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_InvalidLanguage() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields("TEST_SCREEN", "FR"));
        
        assertEquals("Invalid language code. Supported values: EN, AR", exception.getMessage());
        assertEquals("INVALID_LANGUAGE", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_CaseInsensitiveLanguage() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "en"; // lowercase
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Arrays.asList(fieldOption));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("Test Field", dto.getFieldName()); // Should use English name
    }

    @Test
    void testGetRequestCallbackFields_ComboFieldType() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        configuration.setFieldType("combo");
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Arrays.asList(fieldOption));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("combo", dto.getFieldType());
        assertNotNull(dto.getFieldList());
        assertEquals(1, dto.getFieldList().size());
    }

    @Test
    void testGetRequestCallbackFields_DropdownFieldType() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        configuration.setFieldType("dropdown");
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Arrays.asList(fieldOption));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("dropdown", dto.getFieldType());
        assertNotNull(dto.getFieldList());
    }

    @Test
    void testGetRequestCallbackFields_NonComboFieldType() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        configuration.setFieldType("text");
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("text", dto.getFieldType());
        assertNull(dto.getFieldList()); // Should be null for non-combo fields
    }

    @Test
    void testGetRequestCallbackFields_NoFieldOptions() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        configuration.setFieldType("combo");
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenReturn(Collections.emptyList());

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        
        ConfigurationDto dto = result.getData().get(0);
        assertEquals("combo", dto.getFieldType());
        assertNull(dto.getFieldList()); // Should be null when no options
    }

    @Test
    void testGetRequestCallbackFields_MultipleConfigurations() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        Configuration config2 = new Configuration();
        config2.setId(2L);
        config2.setFieldKey("testField2");
        config2.setEnglishFieldName("Test Field 2");
        config2.setArabicFieldName("حقل الاختبار 2");
        config2.setFieldOption("O");
        config2.setFieldLength("100");
        config2.setFieldValidations("optional");
        config2.setFieldType("textarea");
        config2.setSequence(2);
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(configuration, config2));

        // When
        ApiResponse<ConfigurationDto> result = configurationService.getRequestCallbackFields(screenName, lang);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());
        
        // Verify order by sequence
        ConfigurationDto dto1 = result.getData().get(0);
        ConfigurationDto dto2 = result.getData().get(1);
        assertEquals("testField", dto1.getFieldKey());
        assertEquals("testField2", dto2.getFieldKey());
        assertEquals(1, dto1.getSequence());
        assertEquals(2, dto2.getSequence());
    }

    @Test
    void testGetRequestCallbackFields_RepositoryException() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields(screenName, lang));
        
        assertEquals("Failed to retrieve callback fields", exception.getMessage());
        assertEquals("SYSTEM_ERROR", exception.getErrorCode());
    }

    @Test
    void testGetRequestCallbackFields_MappingException() {
        // Given
        String screenName = "TEST_SCREEN";
        String lang = "EN";
        
        // Create a configuration that will cause mapping exception
        Configuration badConfig = new Configuration();
        badConfig.setId(1L);
        badConfig.setFieldKey("testField");
        badConfig.setEnglishFieldName("Test Field");
        badConfig.setArabicFieldName("حقل الاختبار");
        badConfig.setFieldOption("M");
        badConfig.setFieldLength("50");
        badConfig.setFieldValidations("required");
        badConfig.setFieldType("combo"); // Changed to combo to trigger field options lookup
        badConfig.setSequence(1);
        
        when(screenDetailsRepository.findByScreenNameIgnoreCaseAndIsActive(screenName, true))
                .thenReturn(Optional.of(screenDetails));
        when(configurationRepository.findByScreenIdAndIsActiveOrderBySequence(1L, true))
                .thenReturn(Arrays.asList(badConfig));
        when(configurationFieldOptionsRepository.findByConfigurationIdAndIsActive(1L, true))
                .thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> 
                configurationService.getRequestCallbackFields(screenName, lang));
        
        assertEquals("Error processing configuration data", exception.getMessage());
        assertEquals("MAPPING_ERROR", exception.getErrorCode());
    }
}
