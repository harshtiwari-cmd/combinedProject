package com.digi.common.adapter.api.service;


import com.digi.common.domain.model.dto.DeviceInfo;
import com.digi.common.domain.model.dto.UserLookupResponse;
import com.digi.common.domain.model.dto.UserServiceRequest;

public interface UserServiceClient {
    UserLookupResponse getUserByCustomerNumber(String unit, String channel, String acceptLanguage,
                                               String serviceId, String screenId, String moduleId,
                                               String subModuleId, String customerNumber, UserServiceRequest userServiceRequest);
}


