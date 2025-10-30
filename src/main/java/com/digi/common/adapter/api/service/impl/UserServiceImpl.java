package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.UserService;
import com.digi.common.domain.model.RuleEntity;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.domain.repository.RuleRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private RuleRepository ruleRepository;

    @Override
    public GenericResponse<List<RuleDTO>> getRules(String type, String lang, RequestDto requestDto) {
        log.info("Entering getRules() with type='{}', lang='{}', requestId='{}'",
                type, lang, requestDto != null ? requestDto.getRequestInfoDto().getId() : "N/A");

        try {
            // --- Validate Type ---
            if (!"username".equalsIgnoreCase(type) && !"password".equalsIgnoreCase(type)) {
                log.warn("Invalid 'type' parameter received: {}", type);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC),
                        null
                );
            }

            // --- Validate and Map Language ---
            String languageCode = mapLanguage(lang);
            if (languageCode == null) {
                log.warn("Invalid language code received: {}", lang);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC + ": Allowed values 'en', 'ar'"),
                        null
                );
            }

            // --- Fetch Active Rules ---
            List<RuleEntity> rulesFromDb = ruleRepository.findByTypeAndLanguageAndStatusTrue(type.toLowerCase(), languageCode);

            if (rulesFromDb.isEmpty()) {
                log.warn("No active rules found for type='{}' and language='{}'", type, languageCode);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.NOT_FOUND_CODE, AppConstant.NOT_FOUND_DESC),
                        null
                );
            }

            // --- Convert to DTO (no status) ---
            List<RuleDTO> rules = rulesFromDb.stream()
                    .map(rule -> new RuleDTO(rule.getDescription(), rule.getPattern()))
                    .collect(Collectors.toList());

            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                    rules
            );

        } catch (Exception e) {
            log.error("Unexpected error in getRules(): {}", e.getMessage(), e);
            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC),
                    null
            );
        }
    }

    // --- Helper method to map language names to codes ---
    private String mapLanguage(String lang) {
        if (lang == null || lang.isEmpty()) {
            return null;
        }

        lang = lang.trim().toLowerCase();

        switch (lang) {
            case "en":
            case "english":
                return "en";
            case "ar":
            case "arabic":
                return "ar";
            default:
                return null; // invalid language
        }
    }
}
