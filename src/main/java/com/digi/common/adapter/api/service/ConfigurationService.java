package com.digi.common.adapter.api.service;

import com.digi.common.dto.ApiResponse;
import com.digi.common.dto.ConfigurationDto;

public interface ConfigurationService {
    ApiResponse<ConfigurationDto> getRequestCallbackFields(String screenName, String lang);
}
