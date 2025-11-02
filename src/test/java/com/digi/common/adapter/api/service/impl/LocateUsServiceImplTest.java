package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.repository.RbxTLocatorNewRepository;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.domain.repository.LocateUsImagesRepository;
import com.digi.common.infrastructure.persistance.LocateUsImages;
import com.digi.common.infrastructure.persistance.RbxTLocatorNewEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.auditing.DateTimeProvider;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocateUsServiceImplTest {

    @Mock
    private RbxTLocatorNewRepository repository;

    @Mock
    private LocateUsImagesRepository imagesRepository;

    @Mock
    private DateTimeProvider dateTimeProvider;

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
    void fetchAllTypesAsync_Success() throws Exception {
        when(repository.findAll()).thenReturn(List.of(branchEntity, atmEntity, kioskEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsKey("branches"));
        assertTrue(result.containsKey("atms"));
        assertTrue(result.containsKey("kiosks"));

        List<LocateUsDTO> branches = result.get("branches");
        List<LocateUsDTO> atms = result.get("atms");
        List<LocateUsDTO> kiosks = result.get("kiosks");

        assertEquals(1, branches.size());
        assertEquals("BRANCH", branches.get(0).getLocatorType());
        assertEquals("123 Main St, Doha", branches.get(0).getFullAddress());
        assertEquals("Doha", branches.get(0).getCity());


        assertEquals(1, atms.size());
        assertEquals("ATM", atms.get(0).getLocatorType());
        assertEquals("Doha", atms.get(0).getCity());

        assertEquals(1, kiosks.size());
        assertEquals("KIOSK", kiosks.get(0).getLocatorType());
        assertEquals("Doha", kiosks.get(0).getCity());


        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_EmptyResult() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsKey("branches"));
        assertTrue(result.containsKey("atms"));
        assertTrue(result.containsKey("kiosks"));
        assertTrue(result.get("branches").isEmpty());
        assertTrue(result.get("atms").isEmpty());
        assertTrue(result.get("kiosks").isEmpty());
        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_ArabicResult() throws Exception {
        when(repository.findAll()).thenReturn(List.of(branchEntity, atmEntity, kioskEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("ar");
        Map<String, List<LocateUsDTO>> result = future.get();

        assertNotNull(result);
        assertEquals(3, result.size());

        List<LocateUsDTO> branches = result.get("branches");
        List<LocateUsDTO> atms = result.get("atms");
        List<LocateUsDTO> kiosks = result.get("kiosks");

        assertEquals(1, branches.size());
        assertEquals("الفرع الرئيسي", branches.get(0).getName());
        assertEquals("123 الشارع الرئيسي، الدوحة", branches.get(0).getFullAddress());
        assertEquals("الدوحة", branches.get(0).getCity());
        assertEquals("الإثنين-الجمعة: ٩:٠٠ ص - ٥:٠٠ م", branches.get(0).getWorkingHours());

        assertEquals(1, atms.size());
        assertEquals("جهاز صراف آلي ١", atms.get(0).getName());
        assertEquals("456 الشارع الرئيسي، الدوحة", atms.get(0).getFullAddress());
        assertEquals("الدوحة", atms.get(0).getCity());
        assertEquals("٢٤/٧", atms.get(0).getWorkingHours());

        assertEquals(1, kiosks.size());
        assertEquals("كشك ١", kiosks.get(0).getName());
        assertEquals("789 الشارع الرئيسي، الدوحة", kiosks.get(0).getFullAddress());
        assertEquals("الدوحة", kiosks.get(0).getCity());
        assertEquals("الإثنين-الأحد: ٨:٠٠ ص - ٨:٠٠ م", kiosks.get(0).getWorkingHours());

        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_NullCoordinates() throws Exception {
        atmEntity.setLatitude(null);
        atmEntity.setLongitude(null);
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        List<LocateUsDTO> atms = result.get("atms");
        assertEquals(1, atms.size());
        CoordinatesDTO coordinates = atms.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_InvalidCoordinates() throws Exception {
        atmEntity.setLatitude("invalid");
        atmEntity.setLongitude("invalid");
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        List<LocateUsDTO> atms = result.get("atms");
        assertEquals(1, atms.size());
        CoordinatesDTO coordinates = atms.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_Status24x7() throws Exception {
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        List<LocateUsDTO> atms = result.get("atms");
        assertEquals(1, atms.size());
        assertEquals("OPEN", atms.get(0).getStatus());
        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_NullWorkingHours() throws Exception {
        atmEntity.setWorkingHours(null);
        atmEntity.setTiming(null);
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        List<LocateUsDTO> atms = result.get("atms");
        assertEquals(1, atms.size());
        assertEquals("UNKNOWN", atms.get(0).getStatus());
        verify(repository).findAll();
    }

    @Test
    void fetchAllTypesAsync_BlankWorkingHours() throws Exception {
        atmEntity.setWorkingHours("");
        atmEntity.setTiming("");
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> future = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> result = future.get();

        List<LocateUsDTO> atms = result.get("atms");
        assertEquals(1, atms.size());
        assertEquals("CLOSED", atms.get(0).getStatus());
        verify(repository).findAll();
    }

    @Test
    void parseCoordinates_ValidCoordinates() throws ExecutionException, InterruptedException {
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> fetched = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> listMap = fetched.get();
        List<LocateUsDTO> dtoList = listMap.get("atms");

        CoordinatesDTO coordinates = dtoList.get(0).getCoordinates();
        assertEquals(25.276987, coordinates.getLatitude());
        assertEquals(51.520007, coordinates.getLongitude());
        verify(repository).findAll();
    }

    @Test
    void parseCoordinates_InvalidCoordinates() throws ExecutionException, InterruptedException {
        atmEntity.setLatitude("invalid");
        atmEntity.setLongitude("invalid");
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> fetched = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> listMap = fetched.get();

        List<LocateUsDTO> dtoList = listMap.get("atms");
        CoordinatesDTO coordinates = dtoList.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findAll();
    }

    @Test
    void parseCoordinates_NullCoordinates() throws ExecutionException, InterruptedException {
        atmEntity.setLatitude(null);
        atmEntity.setLongitude(null);
        when(repository.findAll()).thenReturn(List.of(atmEntity));

        CompletableFuture<Map<String, List<LocateUsDTO>>> fetched = service.fetchAllTypesAsync("en");
        Map<String, List<LocateUsDTO>> listMap = fetched.get();
        List<LocateUsDTO> dtoList = listMap.get("atms");
        CoordinatesDTO coordinates = dtoList.get(0).getCoordinates();
        assertEquals(0.0, coordinates.getLatitude());
        assertEquals(0.0, coordinates.getLongitude());
        verify(repository).findAll();
    }

    @Test
    void getImageForType_Success() {
        LocateUsImages locateUsImages = new LocateUsImages();
        locateUsImages.setId(1);
        locateUsImages.setLocatorType("BRANCH");
        locateUsImages.setImage("base64BranchImage");
        when(imagesRepository.findByLocatorType("BRANCH")).thenReturn(locateUsImages);

        String result = service.getImageForType("BRANCH");

        assertEquals("base64BranchImage", result);
        verify(imagesRepository).findByLocatorType("BRANCH");
    }
}