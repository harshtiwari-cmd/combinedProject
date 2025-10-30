package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.GeneralService;
import com.digi.common.domain.model.dto.GenaralServiceAuthorizationResData;
import com.digi.common.domain.model.dto.GeneralServiceAuthorizationReq;
import com.digi.common.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeneralServiceImpl implements GeneralService {

    @Override
    public GenericResponse<GenaralServiceAuthorizationResData> getGeneralServiceAuthorization(String unit, String channel, String lang, String serviceId, GeneralServiceAuthorizationReq request) {

        return null;
    }
}
