package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.CoordinatesDTO;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocateUs.class)
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


        Deviceinfo  deviceinfo = Deviceinfo
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

        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(List.of(branchDTO));
        when(locateUsService.fetchByType("ATM", "en")).thenReturn(List.of(atmDto));
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(List.of(kioskDto));

        mockMvc.perform(
                        post("/locate-us")
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
                .andExpect(jsonPath("$.data[0].branches[0].locatorType").value("BRANCH"))
                .andExpect(jsonPath("$.data[0].branches[0].city").value("Doha"));

    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyBranchesExist() throws Exception {
        when(locateUsService.fetchByType("ATM", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(List.of(branchDTO));

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
                .andExpect(jsonPath("$.data[0].branches").isArray())
                .andExpect(jsonPath("$.data[0].branches[0].city").value("Doha"))
                .andExpect(jsonPath("$.data[0].branches[0].country").value("Qatar"))
                .andExpect(jsonPath("$.data[0].branches[0].userCreate").value("APPDATA"));
    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyAtmsExist() throws Exception {

        when(locateUsService.fetchByType("ATM", "en")).thenReturn(List.of(atmDto));
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(Collections.emptyList());


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
                .andExpect(jsonPath("$.data[1].atms[0].locatorType").value("ATM"))
                .andExpect(jsonPath("$.data[1].atms[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data[1].atms[0].fullAddress").value("DUKHAN - AMAN HOSPITAL"));
    }

    @Test
    void getService_ShouldReturnSuccess_WhenOnlyKiosksExist() throws Exception {
        when(locateUsService.fetchByType("ATM", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(List.of(kioskDto));
        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(Collections.emptyList());


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
                .andExpect(jsonPath("$.data[2].kiosks").isArray())
                .andExpect(jsonPath("$.data[2].kiosks[0].locatorType").value("KIOSK"))
                .andExpect(jsonPath("$.data[2].kiosks[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data[2].kiosks[0].workingHours").value("24/7"));

    }

    @Test
    void getService_ShouldReturnException_WhenFetchingKiosk() throws Exception {
        when(locateUsService.fetchByType("ATM", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("KIOSK", "en")).thenThrow(new RuntimeException("Database Error"));
        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(Collections.emptyList());


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

                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value("KIOSK_ERROR"))
                .andExpect(jsonPath("$.status.description").value("Failed to fetch kiosks"))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @Test
    void getService_ShouldReturnException_WhenFetchingBranches() throws Exception {

        when(locateUsService.fetchByType("ATM", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("BRANCH", "en")).thenThrow(new RuntimeException("Database Error"));

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

                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value("BRANCH_ERROR"))
                .andExpect(jsonPath("$.status.description").value("Failed to fetch branches"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }


    @Test
    void getService_ShouldReturnException_WhenFetchingAtms() throws Exception {

        when(locateUsService.fetchByType("ATM", "en")).thenThrow(new RuntimeException("Database Error"));
        when(locateUsService.fetchByType("KIOSK", "en")).thenReturn(Collections.emptyList());
        when(locateUsService.fetchByType("BRANCH", "en")).thenReturn(Collections.emptyList());
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

                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value("ATM_ERROR"))
                .andExpect(jsonPath("$.status.description").value("Failed to fetch ATMs"))
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
                .andExpect(jsonPath("$.status.code").value("G-00000"))
                .andExpect(jsonPath("$.status.description").value("Unsupported language. Use 'ar' or 'en'."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
