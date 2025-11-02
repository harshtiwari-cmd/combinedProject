package com.digi.common.adapter.api.controller;

import com.digi.common.adapter.api.service.ProfileRestService;
import com.digi.common.constants.AppConstants;
import com.digi.common.domain.model.dto.PersonalizationResponse;
import com.digi.common.dto.GenericResponse;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/common")
public class ProfileController {

    @Autowired
    private ProfileRestService profileService;

    @PostMapping("/updateprofile")
    public GenericResponse<Object> updateNickNameAndPictureForProfile(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId,
            @RequestBody Map<String, Object> requestBody) {

        var nickname = (String) requestBody.get("nickname");
        var profilepic = (String) requestBody.get("profilepic");
        return profileService.updateNickNameAndPictureForProfile(unit, channel, lang, serviceId, screenId, moduleId, subModuleId, nickname, profilepic);
    }

    @PostMapping("/viewprofile")
    public GenericResponse<Map<String, PersonalizationResponse>> viewProfile(
            @RequestHeader(name = AppConstants.UNIT) String unit,
            @RequestHeader(name = AppConstants.CHANNEL) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE) String lang,
            @RequestHeader(name = AppConstants.SERVICEID) String serviceId,
            @RequestHeader(name = AppConstants.SCREENID) String screenId,
            @RequestHeader(name = AppConstants.MODULE_ID) String moduleId,
            @RequestHeader(name = AppConstants.SUB_MODULE_ID) String subModuleId) throws JsonParseException {

        return profileService.viewProfile(unit, channel, lang, serviceId, screenId, moduleId, subModuleId);
    }
}