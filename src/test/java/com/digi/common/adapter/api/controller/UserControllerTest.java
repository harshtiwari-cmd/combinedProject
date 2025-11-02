package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.UserService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RequestInfoDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.HeaderDeviceConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private static final String PATH = "/user/rules"; // class-level + method mapping

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRules_happyPath_returnsServiceData() throws Exception {
        // Arrange request body
        RequestInfoDto reqInfo = new RequestInfoDto();
        reqInfo.setType("USER_RULES");

        RequestDto requestDto = new RequestDto();
        requestDto.setRequestInfoDto(reqInfo);

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // Arrange service response
        RuleDTO rule = new RuleDTO();
        GenericResponse<List<RuleDTO>> serviceResp = new GenericResponse<>(
                new ResultUtilVO("200", "SUCCESS"),
                List.of(rule)
        );

        when(userService.getRules(eq("USER_RULES"), eq("en"), any(RequestDto.class)))
                .thenReturn(serviceResp);

        // Act + Assert
        mockMvc.perform(
                        post(PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header(AppConstants.SERVICEID, "SERVICE001")
                                .header(AppConstants.MODULE_ID, "MOD001")
                                .header(AppConstants.SUB_MODULE_ID, "SUBMOD001")
                                .header(AppConstants.SCREENID, "SCR001") // NOTE: SCREENID (matches controller)
                                .header(AppConstants.CHANNEL, "MOBILE_APP")
                                .header(AppConstants.ACCEPT_LANGUAGE, "en")
                                .content(jsonBody)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Use recursive JSONPath to avoid depending on exact field name of the wrapper
                .andExpect(jsonPath("$..code").value("200"))
                .andExpect(jsonPath("$..description").value("SUCCESS"))
                .andExpect(jsonPath("$..data").isArray());
    }

    @Test
    void getRules_missingHeaders_returnsBadRequestEnvelope() throws Exception {
        // Arrange request body
        RequestInfoDto reqInfo = new RequestInfoDto();
        reqInfo.setType("USER_RULES");

        RequestDto requestDto = new RequestDto();
        requestDto.setRequestInfoDto(reqInfo);

        String jsonBody = objectMapper.writeValueAsString(requestDto);

        // Send headers as blank so Spring doesn't 400 before controller;
        // let HeaderDeviceConstant flag them as missing.
        try (MockedStatic<HeaderDeviceConstant> mocked =
                     org.mockito.Mockito.mockStatic(HeaderDeviceConstant.class)) {

            mocked.when(() -> HeaderDeviceConstant.missingMandatoryHeaders(
                    anyString(), anyString(), anyString(), anyString(), anyString()
            )).thenReturn(List.of(AppConstants.SERVICEID, AppConstants.MODULE_ID));

            mockMvc.perform(
                            post(PATH) // "/users/rules"
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .accept(MediaType.APPLICATION_JSON)
                                    .header(AppConstants.SERVICEID, " ")     // present but blank
                                    .header(AppConstants.MODULE_ID, " ")     // present but blank
                                    .header(AppConstants.SUB_MODULE_ID, "SUBMOD001")
                                    .header(AppConstants.SCREENID, "SCR001") // matches your controller
                                    .header(AppConstants.CHANNEL, "MOBILE_APP")
                                    .content(jsonBody)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    // ASSERT EXACT WRAPPER FIELDS (status + data)
                    .andExpect(jsonPath("$.status.code").value(AppConstant.BAD_REQUEST_CODE))
                    .andExpect(jsonPath("$.status.description").value(AppConstant.MANDATORY_HEADERS_DESC))
                    .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
        }
    }
}
 