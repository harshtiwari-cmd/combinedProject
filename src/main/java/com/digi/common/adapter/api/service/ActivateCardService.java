package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.ActivateCardRequest;
import com.digi.common.domain.model.dto.ActivateCardResponse;
import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.infrastructure.common.GenericResponse;

public interface ActivateCardService {

    GenericResponse<ActivateCardResponse> createNewPin(
            String unit, String chanel, String lang,
            String serviceId, String screenId,
            String moduleId, String subModuleId,
            ActivateCardRequest request,
            DeviceInfo deviceInfo
    );
}
