package com.digi.common.adapter.api.service.impl;

import com.digi.common.domain.model.RuleEntity;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RequestInfoDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.domain.repository.RuleRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserServiceImpl userService;

    @Mock
    private RuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(ruleRepository);
    }

    @Test
    void getRules_ShouldReturnRules_WhenRulesExist() {
        List<RuleEntity> ruleEntities = List.of(
                new RuleEntity(1L, "username", "en", "Description1", "Pattern1",false),
                new RuleEntity(2L, "username", "en", "Description2", "Pattern2",false)
        );
        when(ruleRepository.findByTypeAndLanguageAndStatusTrue("username", "en"))
                .thenReturn(ruleEntities);

        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        GenericResponse<List<RuleDTO>> response = userService.getRules("username", "en", requestDto);

        assertThat(response).isNotNull();
        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.RESULT_CODE);
        assertThat(response.getData()).hasSize(2);
        assertThat(response.getData().get(0).getRuleDescription()).isEqualTo("Description1");
        assertThat(response.getData().get(0).getValidationPattern()).isEqualTo("Pattern1");
        assertThat(response.getData().get(1).getRuleDescription()).isEqualTo("Description2");
        assertThat(response.getData().get(1).getValidationPattern()).isEqualTo("Pattern2");

        verify(ruleRepository, times(1)).findByTypeAndLanguageAndStatusTrue("username", "en");
    }

    @Test
    void getRules_ShouldReturnNotFound_WhenNoRulesExist() {
        when(ruleRepository.findByTypeAndLanguageAndStatusTrue("username", "en"))
                .thenReturn(new ArrayList<>());

        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        GenericResponse<List<RuleDTO>> response = userService.getRules("username", "en", requestDto);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.NOT_FOUND_CODE);
        assertThat(response.getData()).isNull();

        verify(ruleRepository, times(1)).findByTypeAndLanguageAndStatusTrue("username", "en");
    }

    @Test
    void getRules_ShouldReturnBadRequest_WhenInvalidType() {
        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        GenericResponse<List<RuleDTO>> response = userService.getRules("invalidType", "en", requestDto);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(response.getData()).isNull();

        verifyNoInteractions(ruleRepository);
    }

    @Test
    void getRules_ShouldReturnBadRequest_WhenInvalidLanguage() {
        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        GenericResponse<List<RuleDTO>> response = userService.getRules("username", "invalidLang", requestDto);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(response.getData()).isNull();

        verifyNoInteractions(ruleRepository);
    }

    @Test
    void getRules_ShouldReturnBadRequest_WhenLanguageIsNullOrEmpty() {
        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        // Null language
        GenericResponse<List<RuleDTO>> responseNull = userService.getRules("username", null, requestDto);
        assertThat(responseNull.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(responseNull.getData()).isNull();

        // Empty language
        GenericResponse<List<RuleDTO>> responseEmpty = userService.getRules("username", "", requestDto);
        assertThat(responseEmpty.getStatus().getCode()).isEqualTo(AppConstant.BAD_REQUEST_CODE);
        assertThat(responseEmpty.getData()).isNull();
    }


    @Test
    void getRules_ShouldHandleUnexpectedException() {
        when(ruleRepository.findByTypeAndLanguageAndStatusTrue("username", "en"))
                .thenThrow(new RuntimeException("DB failure"));

        RequestDto requestDto = new RequestDto(new RequestInfoDto("username"), null);

        GenericResponse<List<RuleDTO>> response = userService.getRules("username", "en", requestDto);

        assertThat(response.getStatus().getCode()).isEqualTo(AppConstant.GEN_ERROR_CODE);
        assertThat(response.getData()).isNull();
    }
}
