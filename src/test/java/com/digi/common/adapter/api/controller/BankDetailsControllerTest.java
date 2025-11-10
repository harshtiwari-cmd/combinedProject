package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.BankDetailsService;
import com.digi.common.adapter.api.service.I18Service;
import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.SocialMedia;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankDetailsControllerTest {

    @Mock
    private I18Service i18Service;

    @Mock
    private BankDetailsService bankDetailsService;

    @InjectMocks
    private BankDetailsController bankDetailsController;

    private ObjectMapper objectMapper;
    private CardBinAllWrapper cardBinAllWrapper;
    private BankDetailsResponseDto responseDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        DeviceInfo deviceInfo = DeviceInfo.builder()
                .deviceId("DEVICE123")
                .ipAddress("192.168.1.1")
                .vendorId("VENDOR123")
                .osVersion("1.1.0")
                .osType("Android")
                .appVersion("2.1.0")
                .endToEndId("E2E123")
                .build();

        cardBinAllWrapper = new CardBinAllWrapper();
        cardBinAllWrapper.setRequestInfo(null);
        cardBinAllWrapper.setDeviceInfo(deviceInfo);

        List<SocialMedia> list = new ArrayList<>();
        list.add(SocialMedia.builder().url("https://www.instagram.com/dukhanbank/").name("Instagram").displayOrder(1).build());
        list.add(SocialMedia.builder().url("https://www.snapchat.com/add/dukhanbank").name("snapchat").displayOrder(2).build());

        responseDto = BankDetailsResponseDto.builder()
                .mail("info.dukhanbank.com")
                .contact(4444444L)
                .internationalContact("+97444444")
                .followUs(list)
                .build();
    }

    @Test
    void getBankDetails_Success_ReturnsOkResponse() throws IOException {
        Mockito.when(bankDetailsService.getBankDetails("en")).thenReturn(responseDto);

        ResponseEntity<?> response = bankDetailsController.getBankDetails(
                "test-unit", "web", "en", "service123", "screen123",
                "module123", "submodule123", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(bankDetailsService, times(1)).getBankDetails("en");
    }

    @Test
    void getBankDetails_ServiceThrowsException_ReturnsInternalServerError() throws IOException {
        Mockito.when(bankDetailsService.getBankDetails("en"))
                .thenThrow(new RuntimeException("Database is down"));

        ResponseEntity<?> response = bankDetailsController.getBankDetails(
                "test-unit", "web", "en", "service123", "screen123",
                "module123", "submodule123", cardBinAllWrapper);

        assertEquals(500, response.getStatusCodeValue());
        verify(bankDetailsService, times(1)).getBankDetails("en");
    }

    @Test
    void getBankDetails_ShouldReturnError_WhenLangNotSupported() {
        ResponseEntity<?> response = bankDetailsController.getBankDetails(
                "PRD", "MB", "hindi", "LOGIN", "SC_01",
                "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(500, response.getStatusCodeValue());
    }
}