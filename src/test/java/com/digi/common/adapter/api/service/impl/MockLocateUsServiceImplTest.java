package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.infrastructure.persistance.LocateUsImages;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    void testFetchAllTypesAsync_withValidData_shouldReturnExpectedResults() throws Exception {
        // Arrange — create mock data
        LocateUsDTO branch = LocateUsDTO.builder()
                .locatorType("BRANCH")
                .city("Doha")
                .country("QATAR")
                .fullAddress("Al Sadd Branch")
                .workingHours("Sunday to Thursday: 7:30am - 1:00pm")
                .build();

        LocateUsDTO atm = LocateUsDTO.builder()
                .locatorType("ATM")
                .city("Industrial Area")
                .code("2061")
                .country("QATAR")
                .status("UNKNOWN")
                .build();

        LocateUsDTO kiosk = LocateUsDTO.builder()
                .locatorType("KIOSK")
                .city("Doha")
                .country("QATAR")
                .fullAddress("Dukhan - Alsad")
                .code("4111")
                .build();

        // Mock objectMapper to return the mock data for each type
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any()))
                .thenReturn(List.of(branch))  // for BRANCH
                .thenReturn(List.of(atm))     // for ATM
                .thenReturn(List.of(kiosk));  // for KIOSK

        // Act — call the method under test
        CompletableFuture<Map<String, List<LocateUsDTO>>> future = locateUsService.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        // Assert — verify the results
        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("Doha", result.get("branches").get(0).getCity());
        assertEquals("Industrial Area", result.get("atms").get(0).getCity());
        assertEquals("Dukhan - Alsad", result.get("kiosks").get(0).getFullAddress());

        // Verify ObjectMapper readValue was called 3 times
        verify(objectMapper, times(3)).readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any());
    }

    @Test
    void testFetchByType_withValidAtmArabic_shouldReturnData() throws Exception {
        // Arrange
        LocateUsDTO dto = new LocateUsDTO();
        dto.setLocatorType("ATM");

        List<LocateUsDTO> mockList = List.of(dto);

        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any()))
                .thenReturn(mockList);

        // Act
        List<LocateUsDTO> result = locateUsService.fetchByType("atm", "ar");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ATM", result.get(0).getLocatorType());

        // Verify interaction
        verify(objectMapper, times(1)).readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any());
    }

    @Test
    void testFetchByType_withKiosk_shouldReturnSuccess() throws IOException {
        // Arrange
        LocateUsDTO locateUsDTO = new LocateUsDTO();
        locateUsDTO.setLocatorType("KIOSK");

        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any()))
                .thenReturn(List.of(locateUsDTO));

        // Act
        List<LocateUsDTO> locateUsDTOS = locateUsService.fetchByType("KIOSK", "en");

        // Assert
        assertNotNull(locateUsDTOS);
        assertEquals(1, locateUsDTOS.size());
        assertEquals("KIOSK", locateUsDTOS.get(0).getLocatorType());

        // Verify method call
        verify(objectMapper, times(1))
                .readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any());
    }

    @Test
    void testFetchByType_withUnsupportedType_shouldReturnEmptyList() {
        // Act
        List<LocateUsDTO> result = locateUsService.fetchByType("unsupported", "en");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Ensure no interaction with ObjectMapper (since the type is unsupported)
        verifyNoInteractions(objectMapper);
    }

    @Test
    void testFetchByType_whenIOExceptionOccurs_shouldReturnEmptyList() throws Exception {
        // Arrange
        when(objectMapper.readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any()))
                .thenThrow(new IOException("Mock IO error"));

        // Act
        List<LocateUsDTO> result = locateUsService.fetchByType("ATM", "en");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify readValue was attempted
        verify(objectMapper, times(1))
                .readValue(any(InputStream.class), ArgumentMatchers.<TypeReference<List<LocateUsDTO>>>any());
    }

    @Test
    void testGetImageForType_validLocatorType_returnsImage() throws IOException {
        String locatorType = "ATM";
        LocateUsImages mockImage = new LocateUsImages();
        mockImage.setLocatorType("ATM");
        mockImage.setImage("atm.png");

        List<LocateUsImages> mockList = List.of(mockImage);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockList);

        String result = locateUsService.getImageForType(locatorType);
        assertEquals("atm.png", result);
    }

    @Test
    void testGetImageForType_invalidLocatorType_returnsEmptyString() throws IOException {
        String locatorType = "Invalid";

        LocateUsImages mockImage = new LocateUsImages();
        mockImage.setLocatorType("ATM");
        mockImage.setImage("atm.png");

        List<LocateUsImages> mockList = List.of(mockImage);

        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(mockList);

        String result = locateUsService.getImageForType(locatorType);
        assertEquals("", result);
    }

    @Test
    public void testGetImageForType_nullLocatorType_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            locateUsService.getImageForType(null);
        });
    }

    @Test
    public void testGetImageForType_emptyLocatorType_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            locateUsService.getImageForType("");
        });
    }


    @Test
    void testGetImageForType_ioException_returnsEmptyString() throws IOException {
        String locatorType = "ATM";
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenThrow(new IOException("File not found"));

        String result = locateUsService.getImageForType(locatorType);
        assertEquals("", result);
    }


}