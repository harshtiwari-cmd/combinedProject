package com.digi.common.adapter.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.digi.common.dto.GenericResponse;

public interface AppointmentService {

    GenericResponse<JsonNode> getPurpose(String unit, String channel, String lang, String serviceId, String moduleId,
                                         String subModuleId, String screenId, JsonNode request);

    GenericResponse<JsonNode> submitAppointment(String unit, String channel, String lang, String serviceId, String moduleId,
                                                String subModuleId, String screenId, JsonNode request);
}
