package com.digi.common.adapter.api.service;

import com.digi.common.dto.GenericResponse;

import java.util.Map;

public interface CustomizerService {

	GenericResponse<?> customizer(String unit, String channel, String lang, String serviceId, String moduleId, String subModuleId, String screenId, Map<String, Object> request);
}