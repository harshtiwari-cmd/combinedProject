package com.digi.common.adapter.api.controller;

import com.digi.common.CommonServiceV2Application;
import com.digi.common.adapter.api.service.BankDetailsService;
import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.SocialMedia;
import com.digi.common.infrastructure.common.AppConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = CommonServiceV2Application.class)
@AutoConfigureMockMvc
class BankDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankDetailsService bankDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private CardBinAllWrapper cardBinAllWrapper;

    private BankDetailsResponseDto responseDto;

    @BeforeEach
    public void setUp() {

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


        ArrayList<SocialMedia> list = new ArrayList<>();

        SocialMedia sm1 = SocialMedia.builder()
                .url("https://www.instagram.com/dukhanbank/")
                .name("Instagram")
                .displayOrder(1).build();

        SocialMedia sm2 = SocialMedia.builder()
                .url("https://www.snapchat.com/add/dukhanbank")
                .name("snapchat")
                .displayOrder(2).build();

        list.add(sm1);
        list.add(sm2);

        responseDto = BankDetailsResponseDto.builder()
                .mail("info.dukhanbank.com")
                .contact(4444444L)
                .internationalContact("+97444444")
                .followUs(list)
                .build();
    }


    @Test
    void getBankDetails_Success_ReturnsOkResponse() throws Exception {

        Mockito.when(bankDetailsService.getBankDetails("en")).thenReturn(responseDto);

        mockMvc.perform(post("/bank-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "test-unit")
                        .header("channel", "web")
                        .header("accept-language", "en")
                        .header("serviceId", "service123")
                        .header("screenId", "screen123")
                        .header("moduleId", "module123")
                        .header("subModuleId", "submodule123")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"))
                .andExpect(jsonPath("$.data.mail").value("info.dukhanbank.com"))
                .andExpect(jsonPath("$.data.followUs[0].name").value("Instagram"));

        verify(bankDetailsService, times(1)).getBankDetails("en");

    }

    @Test
    void getBankDetails_PartialHeadersProvided_ReturnsBadRequest() throws Exception {
        Mockito.when(bankDetailsService.getBankDetails("")).thenReturn(responseDto);

        mockMvc.perform(post("/bank-details")
                        // .header("unit", "test-unit") - not passing header
                        .header("channel", "web")
                        .header("accept-language", "en")
                        .header("serviceId", "service123")
                        .header("screenId", "screen123")
                        .header("moduleId", "module123")
                        .header("subModuleId", "submodule123")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status.code").value(AppConstant.CARD_LENGTH_ERROR_CODE))
                .andExpect(jsonPath("$.status.description").value("Required header 'unit' is missing"))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @Test
    void getBankDetails_ServiceThrowsException_ReturnsInternalServerError() throws Exception {
        Mockito.when(bankDetailsService.getBankDetails("en")).thenThrow(new RuntimeException("Database is down"));

        mockMvc.perform(post("/bank-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("unit", "test-unit")
                        .header("channel", "web")
                        .header("accept-language", "en")
                        .header("serviceId", "service123")
                        .header("screenId", "screen123")
                        .header("moduleId", "module123")
                        .header("subModuleId", "submodule123")
                        .content(objectMapper.writeValueAsString(cardBinAllWrapper))
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status.code").value(AppConstant.VALIDATION_FAILURE_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstant.VALIDATION_FAILURE_DESC))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(bankDetailsService, times(1)).getBankDetails("en");
    }

    @Test
    void getBankDetails_shouldReturnException_whenLangIsNotSupported() throws Exception {

        mockMvc.perform(post("/bank-details")
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
                .andExpect(jsonPath("$.status.code").value(AppConstant.CARD_LENGTH_ERROR_CODE))
                .andExpect(jsonPath("$.status.description").value(AppConstant.LANGUAGE_ERROR_DESC))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}