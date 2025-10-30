package com.digi.common.adapter.api.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.digi.common.adapter.api.service.QuickLinkService;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class QuickLinkController {

    @Autowired
    private QuickLinkService quickLinkService;

    @PostMapping("/quicklinks-summary")
    public GenericResponse<Map<String, Object>> quickLinkList(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestBody Map<String, Object> requestBody) {

        return quickLinkService.quickLinkList(unit, channel, lang, serviceId, screenId, moduleId, subModuleId,requestBody);
    }

    @PostMapping("/quicklinks-save")
    public GenericResponse<Map<String, Object>> quickLinkSave(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestBody Map<String, Object> requestBody) throws JsonParseException {

        return quickLinkService.quickLinkSave(unit, channel, lang, serviceId, screenId, moduleId, subModuleId,requestBody);
    }
}