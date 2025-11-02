package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.ConfigurationService;
import com.digi.common.dto.ApiResponse;
import com.digi.common.dto.BaseServiceRequest;
import com.digi.common.dto.ConfigurationDto;
import com.digi.common.infrastructure.annotation.RequireDeviceInfo;
import com.digi.common.infrastructure.common.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequireDeviceInfo
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;

    @PostMapping("/screen_configuration")
    public ResponseEntity<ApiResponse<ConfigurationDto>> getCallbackFields(
            @RequestHeader(name = AppConstant.HEADER_CHANNEL) String channel,
            @RequestHeader(name = AppConstant.HEADER_ACCEPT_LANGUAGE, defaultValue = AppConstant.DEFAULT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstant.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstant.SCREEN_ID) String screenName,
            @RequestHeader(name = AppConstant.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID) String subModuleId,
            @RequestBody BaseServiceRequest baseServiceRequest) {

        ApiResponse<ConfigurationDto> response = configurationService.getRequestCallbackFields(screenName, lang);
        return ResponseEntity.ok(response);
    }
}
