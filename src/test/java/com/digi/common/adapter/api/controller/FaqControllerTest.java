package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.FaqService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.DefaultHeadersDto;
import com.digi.common.domain.model.dto.FaqDTO;
import com.digi.common.domain.model.dto.FaqResponse;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FaqControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FaqService faqService;

    @InjectMocks
    private FaqController faqController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(faqController).build();
    }

    @Test
    void testGetFaqs_WithAcceptLanguage() throws Exception {
        // Mock service response
        FaqDTO faqDTO = new FaqDTO("Question 1", "Answer 1");
        FaqResponse faqResponse = new FaqResponse(Collections.singletonList(faqDTO));
        GenericResponse<FaqResponse> mockResponse = new GenericResponse<>(
                new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC),
                faqResponse
        );

        when(faqService.getFaqs(any(DefaultHeadersDto.class), any(RequestDto.class)))
                .thenReturn(mockResponse);

        RequestDto requestDto = new RequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/faq/view-faqs")
                        .header(AppConstants.SERVICE_ID, "SVC001")
                        .header(AppConstants.MODULE_ID, "MOD001")
                        .header(AppConstants.SUB_MODULE_ID, "SUB001")
                        .header(AppConstants.SCREENID, "SCR001")
                        .header(AppConstants.CHANNEL, "WEB")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstants.RESULT_CODE))
                .andExpect(jsonPath("$.data.faqList[0].question").value("Question 1"))
                .andExpect(jsonPath("$.data.faqList[0].answer").value("Answer 1"));
    }

    @Test
    void testGetFaqs_WithoutAcceptLanguage_UsesDefault() throws Exception {
        // Mock service response
        FaqResponse faqResponse = new FaqResponse(Collections.emptyList());
        GenericResponse<FaqResponse> mockResponse = new GenericResponse<>(
                new ResultUtilVO(AppConstants.RESULT_CODE, AppConstants.RESULT_DESC),
                faqResponse
        );

        when(faqService.getFaqs(any(DefaultHeadersDto.class), any(RequestDto.class)))
                .thenReturn(mockResponse);

        RequestDto requestDto = new RequestDto();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // No "accept-language" header
        mockMvc.perform(post("/faq/view-faqs")
                        .header(AppConstants.SERVICE_ID, "SVC001")
                        .header(AppConstants.MODULE_ID, "MOD001")
                        .header(AppConstants.SUB_MODULE_ID, "SUB001")
                        .header(AppConstants.SCREENID, "SCR001")
                        .header(AppConstants.CHANNEL, "WEB")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value(AppConstants.RESULT_CODE))
                .andExpect(jsonPath("$.data.faqList").isEmpty());
    }

}
 