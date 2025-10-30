package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.repository.RbxTLocatorNewRepository;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.infrastructure.persistance.RbxTLocatorNewEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LocateUsServiceImplTest {

    @Mock
    private RbxTLocatorNewRepository repository;

    @InjectMocks
    private LocateUsServiceImpl service;

    private RbxTLocatorNewEntity branchEntity;
    private RbxTLocatorNewEntity atmEntity;
    private RbxTLocatorNewEntity kioskEntity;

    @BeforeEach
    void setUp() {
        branchEntity = RbxTLocatorNewEntity.builder()
                .locatorType("BRANCH")
                .searchString("Main Branch Doha")
                .latitude("25.276987")
                .longitude("51.520007")
                .facility("Bank Services")
                .address("123 Main St")
                .arabicName("الفرع الرئيسي")
                .city("Doha")
                .cityInArabic("الدوحة")
                .code("BR001")
                .contactDetails("123-456-7890")
                .country("Qatar")
                .disablePeople(1)
                .fullAddress("123 Main St, Doha")
                .fullAddressArb("123 الشارع الرئيسي، الدوحة")
                .onlineLocation(1)
                .timing("Monday-Friday: 9:00 AM - 5:00 PM")
                .typeLocation("Main")
                .workingHours("Monday-Friday: 9:00 AM - 5:00 PM")
                .workingHoursInArb("الإثنين-الجمعة: ٩:٠٠ ص - ٥:٠٠ م")
                .dateCreate(new Date())
                .userCreate("admin")
                .dateModif(new Date())
                .userModif("admin")
                .isActive("Y")
                .build();

        atmEntity = RbxTLocatorNewEntity.builder()
                .locatorType("ATM")
                .searchString("ATM Doha")
                .latitude("25.276987")
                .longitude("51.520007")
                .facility("Cash Withdrawal")
                .address("456 Main St")
                .arabicName("جهاز صراف آلي ١")
                .cashDeposit(1)
                .cashOut(1)
                .chequeDeposit(0)
                .city("Doha")
                .cityInArabic("الدوحة")
                .code("ATM001")
                .contactDetails("123-456-7890")
                .country("Qatar")
                .disablePeople(1)
                .fullAddress("456 Main St, Doha")
                .fullAddressArb("456 الشارع الرئيسي، الدوحة")
                .onlineLocation(1)
                .timing("24/7")
                .workingHours("24/7")
                .workingHoursInArb("٢٤/٧")
                .dateCreate(new Date())
                .userCreate("admin")
                .dateModif(new Date())
                .userModif("admin")
                .maintenanceVendor("VendorX")
                .atmType("Standard")
                .currencySupported("QAR,USD")
                .isActive("Y")
                .installationDate(new Date())
                .build();

        kioskEntity = RbxTLocatorNewEntity.builder()
                .locatorType("KIOSK")
                .searchString("Kiosk Doha")
                .latitude("25.276987")
                .longitude("51.520007")
                .facility("Self-Service")
                .address("789 Main St")
                .arabicName("كشك ١")
                .city("Doha")
                .cityInArabic("الدوحة")
                .code("KSK001")
                .contactDetails("123-456-7890")
                .country("Qatar")
                .fullAddress("789 Main St, Doha")
                .fullAddressArb("789 الشارع الرئيسي، الدوحة")
                .onlineLocation(1)
                .timing("Monday-Sunday: 8:00 AM - 8:00 PM")
                .workingHours("Monday-Sunday: 8:00 AM - 8:00 PM")
                .workingHoursInArb("الإثنين-الأحد: ٨:٠٠ ص - ٨:٠٠ م")
                .dateCreate(new Date())
                .userCreate("admin")
                .dateModif(new Date())
                .userModif("admin")
                .isActive("Y")
                .build();
    }

    @Test
    void fetchByType_Success() {
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");

        assertNotNull(result);
        assertEquals(1, result.size());
        LocateUsDTO dto = result.get(0);
        assertEquals("ATM", dto.getLocatorType());
        assertEquals("Doha", dto.getCity());
        assertEquals("123-456-7890", dto.getContactDetails());
        assertEquals("Qatar", dto.getCountry());
        assertEquals(1, dto.getDisablePeople());
        assertEquals("24/7", dto.getWorkingHours());
        assertEquals("Cash Withdrawal", dto.getFacility());
        assertEquals("Standard", dto.getAtmType());
        assertEquals("Y", dto.getIsActive());
        assertEquals("OPEN", dto.getStatus());

        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void fetchByType_EmptyResult() {
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(Collections.emptyList());

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void fetchByType_ArabicResult() {

        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "ar");

        assertNotNull(result);
        assertEquals(1, result.size());
        LocateUsDTO dto = result.get(0);
        assertEquals("ATM", dto.getLocatorType());
        assertEquals("جهاز صراف آلي ١", result.get(0).getName());
        assertEquals("456 الشارع الرئيسي، الدوحة", result.get(0).getFullAddress());
    }

    @Test
    void calculateStatus_24x7() {
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        assertEquals("OPEN", result.get(0).getStatus());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void calculateStatus_NullWorkingHours() {
        atmEntity.setWorkingHours(null);
        atmEntity.setTiming(null);
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        assertEquals("UNKNOWN", result.get(0).getStatus());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void calculateStatus_BlankWorkingHours() {
        atmEntity.setWorkingHours("");
        atmEntity.setTiming("");
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        assertEquals("CLOSED", result.get(0).getStatus());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void parseCoordinates_ValidCoordinates() {
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        CoordinatesDTO coordinates = result.get(0).getCoordinates();
        assertEquals(25.276987, coordinates.getLatitude());
        assertEquals(51.520007, coordinates.getLongitude());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void parseCoordinates_InvalidCoordinates() {
        atmEntity.setLatitude("invalid");
        atmEntity.setLongitude("invalid");
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        CoordinatesDTO coordinates = result.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

    @Test
    void parseCoordinates_NullCoordinates() {
        atmEntity.setLatitude(null);
        atmEntity.setLongitude(null);
        when(repository.findByLocatorTypeIgnoreCase("ATM")).thenReturn(List.of(atmEntity));

        List<LocateUsDTO> result = service.fetchByType("ATM", "en");
        CoordinatesDTO coordinates = result.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findByLocatorTypeIgnoreCase("ATM");
    }

}