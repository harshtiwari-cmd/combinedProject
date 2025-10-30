package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.UserService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private GenericResponse<List<RuleDTO>> mockResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        RuleDTO ruleDTO = new RuleDTO("Username must have 8 chars", "^[a-zA-Z0-9]{8,}$");
        mockResponse = new GenericResponse<>(
                new ResultUtilVO("000000", "SUCCESS"),
                List.of(ruleDTO)
        );
    }

    @Test
    void testGetRules_Success() throws Exception {
        when(userService.getRules(eq("username"), eq("en"), any(RequestDto.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/user/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AppConstants.SERVICEID, "SVC001")
                        .header(AppConstants.MODULE_ID, "MOD001")
                        .header(AppConstants.SUB_MODULE_ID, "SUB001")
                        .header(AppConstants.SCREENID, "SCR001")
                        .header(AppConstants.CHANNEL, "WEB")
                        .header(AppConstants.ACCEPT_LANGUAGE, "en")
                        .content("{\"requestInfoDto\":{\"type\":\"username\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"))
                .andExpect(jsonPath("$.status.description").value("SUCCESS"))
                .andExpect(jsonPath("$.data[0].ruleDescription").value("Username must have 8 chars"))
                .andExpect(jsonPath("$.data[0].validationPattern").value("^[a-zA-Z0-9]{8,}$"));

        verify(userService, times(1))
                .getRules(eq("username"), eq("en"), any(RequestDto.class));
    }

    @Test
    void testGetRules_DefaultLanguage() throws Exception {
        when(userService.getRules(eq("username"), eq(AppConstants.DEFAULT_LANG), any(RequestDto.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/user/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(AppConstants.SERVICEID, "SVC001")
                        .header(AppConstants.MODULE_ID, "MOD001")
                        .header(AppConstants.SUB_MODULE_ID, "SUB001")
                        .header(AppConstants.SCREENID, "SCR001")
                        .header(AppConstants.CHANNEL, "WEB")
                        // No ACCEPT_LANGUAGE header here to test default
                        .content("{\"requestInfoDto\":{\"type\":\"username\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status.code").value("000000"));

        verify(userService, times(1))
                .getRules(eq("username"), eq(AppConstants.DEFAULT_LANG), any(RequestDto.class));
    }
}
 