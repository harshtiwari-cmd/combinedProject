package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.CardBinValidationRequest;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.SimpleValidationResponse;
import com.digi.common.infrastructure.common.GenericResponse;
import com.digi.common.infrastructure.persistance.CardBinMaster;

import java.util.List;

public interface CardBinValidationService {

    GenericResponse<SimpleValidationResponse> validateCardBin(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, CardBinValidationRequest request, DeviceInfo deviceInfo);

    GenericResponse<List<CardBinMaster>> getActiveBins();
}