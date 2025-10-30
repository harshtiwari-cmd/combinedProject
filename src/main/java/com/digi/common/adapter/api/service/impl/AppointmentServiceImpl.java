package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.AppointmentService;
import com.digi.common.dto.GenericResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Override
    public GenericResponse<JsonNode> getPurpose(String unit, String channel, String lang, String serviceId,
                                                String moduleId, String subModuleId, String screenId, JsonNode request) {

        return null;
    }

    @Override
    public GenericResponse<JsonNode> submitAppointment(String unit, String channel, String lang, String serviceId,
                                                       String moduleId, String subModuleId, String screenId, JsonNode request) {

        return null;
    }
}
