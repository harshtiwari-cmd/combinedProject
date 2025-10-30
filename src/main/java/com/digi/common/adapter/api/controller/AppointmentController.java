package com.digi.common.adapter.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.digi.common.adapter.api.service.AppointmentService;
import com.digi.common.constants.AppConstants;
import com.digi.common.dto.GenericResponse;
import com.digi.common.infrastructure.common.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/purpose")
    public GenericResponse<JsonNode> getPurpose(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestBody JsonNode request) {

        return appointmentService.getPurpose(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }

    @PostMapping("/submit")
    public GenericResponse<JsonNode> submitAppointment(
            @RequestHeader(name = AppConstants.UNIT, required = true) String unit,
            @RequestHeader(name = AppConstants.CHANNEL, required = true) String channel,
            @RequestHeader(name = AppConstants.ACCEPT_LANGUAGE, required = true) String lang,
            @RequestHeader(name = AppConstants.SERVICEID, required = true) String serviceId,
            @RequestHeader(name = AppConstant.MODULE_ID, required = true) String moduleId,
            @RequestHeader(name = AppConstant.SUB_MODULE_ID, required = true) String subModuleId,
            @RequestHeader(name = AppConstant.SCREEN_ID, required = true) String screenId,
            @RequestBody JsonNode request) {

        return appointmentService.submitAppointment(unit, channel, lang, serviceId, moduleId, subModuleId, screenId, request);
    }
}
