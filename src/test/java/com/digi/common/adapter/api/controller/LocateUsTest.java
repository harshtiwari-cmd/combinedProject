package com.digi.common.adapter.api.controller;

import com.digi.common.CommonServiceV2Application;
import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.infrastructure.common.AppConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest(classes = CommonServiceV2Application.class)
@AutoConfigureMockMvc
class LocateUsTest {

    @MockitoBean
    private LocateUsService locateUsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    private CardBinAllWrapper cardBinAllWrapper;

    private LocateUsDTO branchDTO;
    private LocateUsDTO kioskDto;
    private LocateUsDTO atmDto;

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @BeforeEach
    void setUp() throws ParseException {


        DeviceInfo deviceinfo = DeviceInfo
                .builder()
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
        cardBinAllWrapper.setDeviceInfo(deviceinfo);




        // BRANCH
        branchDTO = LocateUsDTO.builder()
                .locatorType("BRANCH")
                .searchString(null)
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.28461111)
                        .longitude(51.50636389)
                        .build())
                .facility(null)
                .cashDeposit(0)
                .cashOut(0)
                .chequeDeposit(0)
                .city("Doha")
                .code("3")
                .contactDetails("8008555.0")
                .country("Qatar")
                .disablePeople(0)
                .fullAddress("Al Sadd Branch")
                .onlineLocation(0)
                .timing("Sunday|7:30|Thursday|13:00")
                .typeLocation(null)
                .workingHours("Sunday to Thursday: 7:30am - 1:00pm\\n")
                .status("CLOSED")
                .dateCreate(format.parse("2024-10-27T08:34:00.000+00:00"))
                .userCreate("APPDATA")
                .dateModif(null)
                .userModif(null)
                .maintenanceVendor(null)
                .atmType("BRANCH")
                .currencySupported(null)
                .isActive(null)
                .installationDate(null)
                .build();

        // KIOSK
        kioskDto = LocateUsDTO.builder()
                .locatorType("KIOSK")
                .searchString(null)
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.28461111)
                        .longitude(51.50636389)
                        .build())
                .facility(null)
                .cashDeposit(0)
                .cashOut(0)
                .chequeDeposit(0)
                .city("DOHA")
                .code("4111")
                .contactDetails("8008555.0")
                .country("Qatar")
                .disablePeople(0)
                .fullAddress("Dukhan - Alsad")
                .onlineLocation(1)
                .timing(null)
                .typeLocation(null)
                .workingHours("24/7")
                .status("OPEN")
                .dateCreate(format.parse("2024-07-09T07:51:00.000+00:00"))
                .userCreate("SYS")
                .dateModif(format.parse("2025-06-01T04:45:00.000+00:00"))
                .userModif("APPDATA")
                .maintenanceVendor(null)
                .atmType("KIOSK")
                .currencySupported(null)
                .isActive(null)
                .installationDate(null)
                .build();

        // ATM
        atmDto = LocateUsDTO.builder()
                .locatorType("ATM")
                .coordinates(CoordinatesDTO.builder()
                        .latitude(25.232232)
                        .longitude(51.574471)
                        .build())
                .cashDeposit(1)
                .cashOut(1)
                .chequeDeposit(1)
                .city("DOHA")
                .code("3055")
                .contactDetails("8008555.0")
                .country("QATAR")
                .disablePeople(0)
                .fullAddress("DUKHAN - AMAN HOSPITAL")
                .onlineLocation(1)
                .workingHours("24/7")
                .status("OPEN")
                .dateCreate(format.parse("2023-12-20T03:41:00.000+00:00"))
                .userCreate("OCIETLUSR")
                .dateModif(format.parse("2024-12-15T06:59:00.000+00:00"))
                .userModif("APPDATA")
                .atmType("ATM")
                .build();
    }

    @Test
    void getService_ShouldReturnSuccess_WhenDataExists() throws Exception {

        // Mock fetchAllTypesAsync
        Map<String, List<LocateUsDTO>> resultMap = new HashMap<>();
        resultMap.put("branches", List.of(branchDTO));
        resultMap.put("atms", List.of(atmDto));
        resultMap.put("kiosks", List.of(kioskDto));

        // Mock getImageForType
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        when(locateUsService.fetchAllTypesAsync(anyString())).thenReturn(CompletableFuture.completedFuture(resultMap));

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper)))

                .andExpect(jsonPath("$.data[0].branches[0].image").value("branch_image_url"))
                .andExpect(jsonPath("$.data[0].branches[1].locatorType").value("BRANCH"))
                .andExpect(jsonPath("$.data[0].branches[1].city").value("Doha"))
                .andExpect(jsonPath("$.data[1].atms[0].image").value("atm_image_url"))
                .andExpect(jsonPath("$.data[1].atms[1].locatorType").value("ATM"))
                .andExpect(jsonPath("$.data[2].kiosks[0].image").value("kiosk_image_url"))
                .andExpect(jsonPath("$.data[2].kiosks[1].locatorType").value("KIOSK"));
    }

    @Test
    void getService_ShouldReturnNoData_WhenAllEmpty() throws Exception {
        Map<String, List<LocateUsDTO>> emptyMap = new HashMap<>();
        emptyMap.put("branches", Collections.emptyList());
        emptyMap.put("atms", Collections.emptyList());
        emptyMap.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(emptyMap));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )
                .andExpect(jsonPath("$.status.code").value(AppConstant.NO_DATA_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstant.NODATA))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyBranchesExist() throws Exception {
        Map<String, List<LocateUsDTO>> resultMap = new HashMap<>();
        resultMap.put("branches", List.of(branchDTO));
        resultMap.put("atms", Collections.emptyList());
        resultMap.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(resultMap));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )

                .andExpect(jsonPath("$.data[0].branches").isArray())
                .andExpect(jsonPath("$.data[0].branches[0].image").value("branch_image_url"))
                .andExpect(jsonPath("$.data[0].branches[1].city").value("Doha"))
                .andExpect(jsonPath("$.data[0].branches[1].country").value("Qatar"))
                .andExpect(jsonPath("$.data[0].branches[1].userCreate").value("APPDATA"))
                .andExpect(jsonPath("$.data[1].atms[0].image").value("atm_image_url"))
                .andExpect(jsonPath("$.data[1].atms.length()").value(1))
                .andExpect(jsonPath("$.data[2].kiosks[0].image").value("kiosk_image_url"))
                .andExpect(jsonPath("$.data[2].kiosks.length()").value(1));

    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyAtmsExist() throws Exception {
        Map<String, List<LocateUsDTO>> resultMap = new HashMap<>();
        resultMap.put("branches", Collections.emptyList());
        resultMap.put("atms", List.of(atmDto));
        resultMap.put("kiosks", Collections.emptyList());

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(resultMap));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"))
                .andExpect(jsonPath("$.data[1].atms").isArray())
                .andExpect(jsonPath("$.data[1].atms[0].image").value("atm_image_url"))
                .andExpect(jsonPath("$.data[1].atms[1].locatorType").value("ATM"))
                .andExpect(jsonPath("$.data[1].atms[1].status").value("OPEN"))
                .andExpect(jsonPath("$.data[1].atms[1].fullAddress").value("DUKHAN - AMAN HOSPITAL"))
                .andExpect(jsonPath("$.data[0].branches[0].image").value("branch_image_url"))
                .andExpect(jsonPath("$.data[0].branches.length()").value(1))
                .andExpect(jsonPath("$.data[2].kiosks[0].image").value("kiosk_image_url"))
                .andExpect(jsonPath("$.data[2].kiosks.length()").value(1));
    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyKiosksExist() throws Exception {
        Map<String, List<LocateUsDTO>> resultMap = new HashMap<>();
        resultMap.put("branches", Collections.emptyList());
        resultMap.put("atms", Collections.emptyList());
        resultMap.put("kiosks", List.of(kioskDto));

        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.completedFuture(resultMap));
        when(locateUsService.getImageForType("BRANCH")).thenReturn("branch_image_url");
        when(locateUsService.getImageForType("ATM")).thenReturn("atm_image_url");
        when(locateUsService.getImageForType("KIOSK")).thenReturn("kiosk_image_url");

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"))
                .andExpect(jsonPath("$.data[2].kiosks").isArray())
                .andExpect(jsonPath("$.data[2].kiosks[0].image").value("kiosk_image_url"))
                .andExpect(jsonPath("$.data[2].kiosks[1].locatorType").value("KIOSK"))
                .andExpect(jsonPath("$.data[2].kiosks[1].status").value("OPEN"))
                .andExpect(jsonPath("$.data[2].kiosks[1].workingHours").value("24/7"))
                .andExpect(jsonPath("$.data[0].branches[0].image").value("branch_image_url"))
                .andExpect(jsonPath("$.data[0].branches.length()").value(1))
                .andExpect(jsonPath("$.data[1].atms[0].image").value("atm_image_url"))
                .andExpect(jsonPath("$.data[1].atms.length()").value(1));
    }

    @Test
    void getService_ShouldReturnException_WhenFetchingFails() throws Exception {
        when(locateUsService.fetchAllTypesAsync("en")).thenReturn(CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Database Error");
        }));

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "en")
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper)))
                .andExpect(jsonPath("$.status.code").value(AppConstant.VALIDATION_FAILURE_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstant.VALIDATION_FAILURE_DESC))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @Test
    void getService_shouldReturnException_whenLangIsNotSupported() throws Exception {

        mockMvc.perform(post("/locate-us")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "PRD")
                        .header("channel", "MB")
                        .header("accept-language", "hindi") // - trying with hindi
                        .header("serviceId", "LOGIN")
                        .header("screenId", "SC_01")
                        .header("moduleId", "MI_01")
                        .header("subModuleId", "SMI_01")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value(AppConstant.LANGUAGE_ERROR))
                .andExpect(jsonPath("$.status.description").value(AppConstant.LANGUAGE_ERROR_DESC))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
