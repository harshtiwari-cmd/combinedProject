package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.CardStatusResponse;
import com.digi.common.domain.model.dto.CardStatusValidationRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.infrastructure.common.GenericResponse;

public interface CardStatusService {

	GenericResponse<CardStatusResponse> validateCardStatus(
			String unit,
			String channel,
			String acceptLanguage,
			String serviceId,
			String screenId,
			String moduleId,
			String subModuleId,
			CardStatusValidationRequest request,
			DeviceInfo deviceInfo
	);
}


