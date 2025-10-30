package com.digi.common.adapter.api.service;

import com.digi.common.dto.GenericResponse;

import java.util.Map;

public interface QuickLinkService {

    GenericResponse<Map<String, Object>> quickLinkList(String unit, String channel, String lang,
                                                       String serviceId, String screenId, String moduleId, String subModuleId, Map<String, Object> requestBody);

    GenericResponse<Map<String, Object>> quickLinkSave(String unit, String channel, String lang, String serviceId,
                                                       String screenId, String moduleId, String subModuleId, Map<String, Object> requestBody);

}
