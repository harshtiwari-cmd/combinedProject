package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.LocateUsService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.CardBinAllWrapper;
import com.digi.common.domain.model.dto.LocateUsDTO;
import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.ResultUtilVO;
import com.digi.common.infrastructure.common.AppConstant;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/locate-us")
public class LocateUs {

    @Autowired
    private LocateUsService locateUsService;

    private static final Set<String> SUPPORTED_LANGUAGES = AppConstant.SUPPORTED_LANGUAGES;


    @PostMapping
    public ResponseEntity<GenericResponse<List<Map<String, List<Object>>>>> getService(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE,required = false) String lang,
            @RequestHeader(name = AppConstants.SERVICEID,required = true) String serviceId,
            @RequestHeader(name = AppConstants.SCREEN_ID,required = true) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID, required = true) String subModuleId,
            @Valid @RequestBody(required = true) CardBinAllWrapper wrapper
    ) {
        log.info("GET /locate-us - Received request to fetch all services");

        String language = (Objects.nonNull(lang) && !lang.trim().isEmpty()) ? lang.trim().toLowerCase() : "en";
        if (!SUPPORTED_LANGUAGES.contains(language)) {
            log.warn("Unsupported language received: {}", lang);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(new ResultUtilVO(AppConstant.LANGUAGE_ERROR, AppConstant.LANGUAGE_ERROR_DESC), null));
        }

        try {
            CompletableFuture<Map<String, List<LocateUsDTO>>> allTypesFuture = locateUsService.fetchAllTypesAsync(language);
            Map<String, List<LocateUsDTO>> allTypes = allTypesFuture.get();

            List<LocateUsDTO> branches = allTypes.get(AppConstant.BRANCHES);
            List<LocateUsDTO> atms = allTypes.get(AppConstant.ATMS);
            List<LocateUsDTO> kiosks = allTypes.get(AppConstant.KIOSKS);

            if (branches.isEmpty() && atms.isEmpty() && kiosks.isEmpty()) {
                log.warn("Failed to load: no data found");
                return ResponseEntity.ok(new GenericResponse<>(new ResultUtilVO(AppConstant.NO_DATA_CODE, AppConstant.NODATA), new ArrayList<>()));
            }

            List<Map<String, List<Object>>> data = new ArrayList<>();
            List<Object> branchesList = new ArrayList<>();
            branchesList.add(Collections.singletonMap(AppConstant.IMAGE, locateUsService.getImageForType("BRANCH")));
            branchesList.addAll(branches);

            data.add(Collections.singletonMap(AppConstant.BRANCHES, branchesList));
            List<Object> atmsList = new ArrayList<>();
            atmsList.add(Collections.singletonMap(AppConstant.IMAGE, locateUsService.getImageForType("ATM")));
            atmsList.addAll(atms);

            data.add(Collections.singletonMap(AppConstant.ATMS, atmsList));
            List<Object> kiosksList = new ArrayList<>();
            kiosksList.add(Collections.singletonMap(AppConstant.IMAGE, locateUsService.getImageForType("KIOSK")));
            kiosksList.addAll(kiosks);

            data.add(Collections.singletonMap(AppConstant.KIOSKS, kiosksList));
            GenericResponse<List<Map<String, List<Object>>>> response =
                    new GenericResponse<>(new ResultUtilVO(AppConstant.RESULT_CODE,AppConstant.SUCCESS), data);
            log.info("Successfully fetched all data");
            return ResponseEntity.ok(response);

        } catch (ExecutionException ee) {
            Throwable cause = ee.getCause();
            log.error("Exception occurred while fetching services: {}", cause.getMessage(), cause);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(new ResultUtilVO(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC), null));
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", ie);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(new ResultUtilVO(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC), null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(new ResultUtilVO(AppConstant.VALIDATION_FAILURE_CODE, AppConstant.VALIDATION_FAILURE_DESC), null));
        }
    }
}