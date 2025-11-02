package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocateUsControllerMockitoTest {

    @Mock
    private LocateUsService locateUsService;

    @InjectMocks
    private LocateUs locateUsController;

    private ObjectMapper objectMapper;
    private CardBinAllWrapper cardBinAllWrapper;
    private LocateUsDTO branchDTO;
    private LocateUsDTO kioskDto;
    private LocateUsDTO atmDto;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @BeforeEach
    void setUp() throws ParseException {
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

        branchDTO = LocateUsDTO.builder()
                .locatorType("BRANCH")
                .city("Doha")
                .country("Qatar")
                .userCreate("APPDATA")
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.28461111)
                        .longitude(51.50636389)
                        .build())
                .build();

        kioskDto = LocateUsDTO.builder()
                .locatorType("KIOSK")
                .city("Doha")
                .status("OPEN")
                .workingHours("24/7")
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.28461111)
                        .longitude(51.50636389)
                        .build())
                .build();

        atmDto = LocateUsDTO.builder()
                .locatorType("ATM")
                .status("OPEN")
                .fullAddress("DUKHAN - AMAN HOSPITAL")
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.232232)
                        .longitude(51.574471)
                        .build())
                .build();
    }

    @Test
    void getLocateUs_Success_WhenAllTypesExist() throws Exception {
        Map<String, List<LocateUsDTO>> data = new HashMap<>();
        data.put("branches", List.of(branchDTO));
        data.put("atms", List.of(atmDto));
        data.put("kiosks", List.of(kioskDto));

        when(locateUsService.fetchAllTypesAsync(anyString())).thenReturn(CompletableFuture.completedFuture(data));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Test
    void getLocateUs_Success_WhenOnlyBranchesExist() throws Exception {
        Map<String, List<LocateUsDTO>> data = new HashMap<>();
        data.put("branches", List.of(branchDTO));
        data.put("atms", Collections.emptyList());
        data.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(data));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Disabled
    void getLocateUs_Success_WhenOnlyAtmsExist() throws Exception {
        Map<String, List<LocateUsDTO>> data = new HashMap<>();
        data.put("branches", Collections.emptyList());
        data.put("atms", List.of(atmDto));
        data.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(data));
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Disabled
    void getLocateUs_Success_WhenOnlyKiosksExist() throws Exception {
        Map<String, List<LocateUsDTO>> data = new HashMap<>();
        data.put("branches", Collections.emptyList());
        data.put("atms", Collections.emptyList());
        data.put("kiosks", List.of(kioskDto));

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(data));
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Test
    void getLocateUs_NoData_ReturnsNoDataCode() throws Exception {
        Map<String, List<LocateUsDTO>> emptyMap = new HashMap<>();
        emptyMap.put("branches", Collections.emptyList());
        emptyMap.put("atms", Collections.emptyList());
        emptyMap.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(emptyMap));

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(200, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Disabled
    void getLocateUs_ServiceThrowsException_ReturnsInternalServerError() {
        when(locateUsService.fetchAllTypesAsync("en"))
                .thenThrow(new RuntimeException("Database Error"));

        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "en", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(500, response.getStatusCodeValue());
        verify(locateUsService, times(1)).fetchAllTypesAsync("en");
    }

    @Test
    void getLocateUs_ShouldReturnError_WhenLangNotSupported() {
        ResponseEntity<?> response = locateUsController.getService(
                "PRD", "MB", "hindi", "LOGIN", "SC_01", "MI_01", "SMI_01", cardBinAllWrapper);

        assertEquals(500, response.getStatusCodeValue());
    }
}