package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.BankDetailsService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.BankDetailsResponseDto;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


@Slf4j
@RequestMapping("/bank-details")
@RestController
public class BankDetailsController {

    @Autowired
    private BankDetailsService bankDetailsService;

    private static final Set<String> SUPPORTED_LANGUAGES = AppConstant.SUPPORTED_LANGUAGES;

    @PostMapping
    public ResponseEntity<GenericResponse<BankDetailsResponseDto>> getBankDetails(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE,required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID,required = true) String serviceId,
            @RequestHeader(name = AppConstants.SCREEN_ID,required = true) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID, required = true) String subModuleId,
            @Valid @RequestBody(required = true) CardBinAllWrapper wrapper
    ) {
        log.info("Received request to fetch bank details");

        String language = (lang == null || lang.trim().isEmpty()) ? "en" : lang.trim().toLowerCase();
        if (!SUPPORTED_LANGUAGES.contains(language)) {
            log.warn("Unsupported language received: {}", lang);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(new ResultUtilVO( AppConstant.CARD_LENGTH_ERROR_CODE, AppConstant.LANGUAGE_ERROR_DESC), null));
        }

        try {
            BankDetailsResponseDto data = bankDetailsService.getBankDetails(language);
            log.info("Bank details fetched successfully for email: {}", data.getMail());

            GenericResponse<BankDetailsResponseDto> response =
                    new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE, AppConstant.SUCCESS), data);

            return ResponseEntity.ok(response);


        } catch (Exception e) {
            log.error("Error fetching bank details: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(new ResultUtilVO(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC), null));
        }
    }
}
