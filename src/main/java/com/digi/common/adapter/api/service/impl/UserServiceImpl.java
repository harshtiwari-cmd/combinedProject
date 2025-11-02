package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.UserService;
import com.digi.common.domain.model.RuleEntity;
import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.domain.repository.RuleRepository;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import com.digi.common.infrastructure.common.HeaderDeviceConstant;
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
        final String reqId = (requestDto != null && requestDto.getRequestInfoDto() != null)
                ? String.valueOf(requestDto.getRequestInfoDto().getId())
                : "N/A";
        log.info("Entering getRules() type='{}', lang='{}', requestId='{}'", type, lang, reqId);

        try {
            // 000400: invalid type
            if (!"username".equalsIgnoreCase(type) && !"password".equalsIgnoreCase(type)) {
                log.warn("Invalid 'type' parameter: {}", type);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.BAD_REQUEST_DESC + ": Invalid type"),
                        null
                );
            }

            // 000400: invalid language (via helper)
            String languageCode = HeaderDeviceConstant.mapLanguage(lang);

            //000400: RequestInfoDto Not Found
            if (HeaderDeviceConstant.checkRequestInfoDto(requestDto.getRequestInfoDto())) {
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.REQUEST_INFO_DESC),
                        null
                );
            }

            // 000400: device info not found (via helper)
            if (!HeaderDeviceConstant.hasValidDeviceInfo(requestDto)) {
                log.warn("Device info not found or incomplete for requestId={}", reqId);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.BAD_REQUEST_CODE, AppConstant.DEVICE_INFO_DESC),
                        null
                );
            }

            // 000409: duplicate (inline stub as before)
            boolean isDuplicate = false;
            if (isDuplicate) {
                log.warn("Duplicate request detected for requestId={}", reqId);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.DUPLICATE_REQUEST_CODE, AppConstant.DUPLICATE_REQUEST_DESC),
                        null
                );
            }

            // Repository call
            List<RuleEntity> rulesFromDb =
                    ruleRepository.findByTypeAndLanguageAndStatusTrue(type.toLowerCase(), languageCode);

            // 000404: not found
            if (rulesFromDb == null || rulesFromDb.isEmpty()) {
                log.warn("No active rules found for type='{}' and lang='{}'", type, languageCode);
                return new GenericResponse<>(
                        new ResultUtilVO(AppConstant.NOT_FOUND_CODE, AppConstant.NOT_FOUND_DESC),
                        null
                );
            }

            // 000000: success
            List<RuleDTO> rules = rulesFromDb.stream()
                    .map(r -> new RuleDTO(r.getDescription(), r.getPattern()))
                    .collect(Collectors.toList());

            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.RESULT_DESC),
                    rules
            );

        } catch (org.springframework.dao.QueryTimeoutException te) {
            log.error("Query timeout for getRules(): {}", te.getMessage(), te);
            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.REQUEST_TIMEOUT_CODE, AppConstant.REQUEST_TIMEOUT_DESC),
                    null
            );

        } catch (org.springframework.dao.DataAccessResourceFailureException ex) {
            log.error("Database/service unavailable in getRules(): {}", ex.getMessage(), ex);
            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.SERVICE_UNAVAILABLE_CODE, AppConstant.SERVICE_UNAVAILABLE_DESC),
                    null
            );

        } catch (Exception e) {
            log.error("Unexpected error in getRules(): {}", e.getMessage(), e);
            return new GenericResponse<>(
                    new ResultUtilVO(AppConstant.GEN_ERROR_CODE, AppConstant.GEN_ERROR_DESC),
                    null
            );
        }

    }
}
