package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.dto.LocateUsDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockLocateUsServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MockLocateUsServiceImpl locateUsService;

    @Test
    void testFetchByType_withValidBranchEnglish_shouldReturnData() throws Exception {
        LocateUsDTO dto = new LocateUsDTO();
        dto.setLocatorType("Mock Branch");
        List<LocateUsDTO> mockList = List.of(dto);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockList);

        List<LocateUsDTO> result = locateUsService.fetchByType("branch", "en");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mock Branch", result.get(0).getLocatorType());
        verify(objectMapper, times(1)).readValue(any(InputStream.class), any(TypeReference.class));
    }

    @Test
    void testFetchByType_withValidAtmArabic_shouldReturnData() throws Exception {
        LocateUsDTO dto = new LocateUsDTO();
        dto.setLocatorType("ATM");
        List<LocateUsDTO> mockList = List.of(dto);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(mockList);

        List<LocateUsDTO> result = locateUsService.fetchByType("atm", "ar");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ATM", result.get(0).getLocatorType());
    }

    @Test
    void testFetchByType_withKiosk_shouldReturnSuccess() throws IOException {
        LocateUsDTO locateUsDTO = new LocateUsDTO();
        locateUsDTO.setLocatorType("KIOSK");

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenReturn(List.of(locateUsDTO));

        List<LocateUsDTO> locateUsDTOS = locateUsService.fetchByType("KIOSK", "en");

        assertNotNull(locateUsDTOS);
        assertEquals(1, locateUsDTOS.size());
        assertEquals("KIOSK", locateUsDTOS.get(0).getLocatorType());
    }

    @Test
    void testFetchByType_withUnsupportedType_shouldReturnEmptyList() {
        List<LocateUsDTO> result = locateUsService.fetchByType("unsupported", "en");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchByType_whenIOExceptionOccurs_shouldReturnEmptyList() throws Exception {
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class)))
                .thenThrow(new IOException("Mock IO error"));

        List<LocateUsDTO> result = locateUsService.fetchByType("branch", "en");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}