package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.RuleEntity;
import com.digi.common.domain.model.dto.DeviceInfoDto;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RequestInfoDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.domain.repository.RuleRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.QueryTimeoutException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private UserServiceImpl userService; // <-- your concrete service implementation

    // --- Helpers

    private RequestDto buildRequestDtoWithDevice(boolean valid) {
        RequestDto dto = new RequestDto();

        // info (id not required, but your service logs it)
        RequestInfoDto info = new RequestInfoDto();
        info.setId(123L);
        // type is passed directly to service param, not read from dto in service
        // but set anyway for completeness
        info.setType("username");
        dto.setRequestInfoDto(info);

        if (valid) {
            DeviceInfoDto device = new DeviceInfoDto();
            device.setDeviceId("dev-1");
            device.setIpAddress("10.0.0.1");
            device.setOsType("Android");
            device.setOsVersion("14");
            device.setAppVersion("2.0.0");
            device.setVendorId("Samsung");
            device.setEndToEndId("E2E-123");
            dto.setDeviceInfo(device);
        } else {
            // invalid: missing or empty device info
            dto.setDeviceInfo(null);
        }
        return dto;
    }

    // --- Tests

    @BeforeEach
    void setup() {
        // nothing special
    }

    @Test
    void getRules_invalidType_returnsBadRequest() {
        // type must be username or password
        String type = "foobar";
        String lang = "en";
        RequestDto dto = buildRequestDtoWithDevice(true);

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp).isNotNull();
        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(resp.getStatus().getDescription()).contains("Bad Request");
        assertThat(resp.getData()).isNull();
    }

    @Test
    void getRules_missingDeviceInfo_returnsBadRequest() {
        String type = "password"; // valid
        String lang = "en";       // valid
        RequestDto dto = buildRequestDtoWithDevice(false); // invalid device

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.DEVICE_INFO_DESC);
        assertThat(resp.getData()).isNull();
    }

    @Test
    void getRules_notFound_returns404Envelope() {
        String type = "username";
        String lang = "en";
        RequestDto dto = buildRequestDtoWithDevice(true);

        when(ruleRepository.findByTypeAndLanguageAndStatusTrue(eq("username"), eq("en")))
                .thenReturn(Collections.emptyList());

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.NOT_FOUND_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.NOT_FOUND_DESC);
        assertThat(resp.getData()).isNull();

        verify(ruleRepository, times(1))
                .findByTypeAndLanguageAndStatusTrue("username", "en");
    }

    @Test
    void getRules_success_returns000000_andMappedDTOs() {
        String type = "password";
        String lang = "ar"; // also valid
        RequestDto dto = buildRequestDtoWithDevice(true);

        // Mock a RuleEntity (concrete class can be mocked by Mockito)
        RuleEntity e1 = mock(RuleEntity.class);
        when(e1.getDescription()).thenReturn("At least 8 chars");
        when(e1.getPattern()).thenReturn("^(?=.{8,}).*$");

        RuleEntity e2 = mock(RuleEntity.class);
        when(e2.getDescription()).thenReturn("Must contain a digit");
        when(e2.getPattern()).thenReturn(".*\\d.*");

        when(ruleRepository.findByTypeAndLanguageAndStatusTrue(eq("password"), eq("ar")))
                .thenReturn(List.of(e1, e2));

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.RESULT_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.RESULT_DESC);
        assertThat(resp.getData()).isNotNull();
        assertThat(resp.getData()).hasSize(2);

        // verify mapping description & pattern
        RuleDTO r1 = resp.getData().get(0);
        RuleDTO r2 = resp.getData().get(1);
        assertThat(r1.getRuleDescription()).isEqualTo("At least 8 chars");
        assertThat(r1.getValidationPattern()).isEqualTo("^(?=.{8,}).*$");
        assertThat(r2.getRuleDescription()).isEqualTo("Must contain a digit");
        assertThat(r2.getValidationPattern()).isEqualTo(".*\\d.*");

        verify(ruleRepository, times(1))
                .findByTypeAndLanguageAndStatusTrue("password", "ar");
    }

    @Test
    void getRules_queryTimeout_returns408Envelope() {
        String type = "username";
        String lang = "en";
        RequestDto dto = buildRequestDtoWithDevice(true);

        when(ruleRepository.findByTypeAndLanguageAndStatusTrue(eq("username"), eq("en")))
                .thenThrow(new QueryTimeoutException("simulated timeout"));

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.REQUEST_TIMEOUT_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.REQUEST_TIMEOUT_DESC);
        assertThat(resp.getData()).isNull();
    }

    @Test
    void getRules_serviceUnavailable_returns503Envelope() {
        String type = "password";
        String lang = "en";
        RequestDto dto = buildRequestDtoWithDevice(true);

        when(ruleRepository.findByTypeAndLanguageAndStatusTrue(eq("password"), eq("en")))
                .thenThrow(new DataAccessResourceFailureException("db down"));

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.SERVICE_UNAVAILABLE_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.SERVICE_UNAVAILABLE_DESC);
        assertThat(resp.getData()).isNull();
    }

    @Test
    void getRules_unexpectedException_returns500Envelope() {
        String type = "password";
        String lang = "en";
        RequestDto dto = buildRequestDtoWithDevice(true);

        when(ruleRepository.findByTypeAndLanguageAndStatusTrue(eq("password"), eq("en")))
                .thenThrow(new RuntimeException("boom"));

        GenericResponse<List<RuleDTO>> resp = userService.getRules(type, lang, dto);

        assertThat(resp.getStatus().getCode()).isEqualTo(AppConstant.GEN_ERROR_CODE);
        assertThat(resp.getStatus().getDescription()).isEqualTo(AppConstant.GEN_ERROR_DESC);
        assertThat(resp.getData()).isNull();
    }
}
