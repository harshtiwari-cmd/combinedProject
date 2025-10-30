package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockBankDetailsImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MockBankDetailsImpl mockBankDetails;

    @Test
    void testGetBankDetails_withEnglishLanguage_shouldReturnResponse() throws Exception {

        String lang = "en";
        BankDetailsResponseDto mockResponse = new BankDetailsResponseDto();
        mockResponse.setMail("info@dukhanbank.com");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockResponse);

        BankDetailsResponseDto result = mockBankDetails.getBankDetails(lang);

        assertNotNull(result);
        assertEquals("info@dukhanbank.com", result.getMail());
        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    void testGetBankDetails_withArabicLanguage_shouldReturnResponse() throws Exception {

        String lang = "ar";
        BankDetailsResponseDto mockResponse = new BankDetailsResponseDto();
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockResponse);

        BankDetailsResponseDto result = mockBankDetails.getBankDetails(lang);

        assertNotNull(result);
        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }


    @Test
    void testGetBankDetails_whenIOExceptionOccurs_shouldReturnNull() throws Exception {

        String lang = "en";
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("Mock IO Exception"));

        BankDetailsResponseDto result = mockBankDetails.getBankDetails(lang);
        assertNull(result);
    }
}