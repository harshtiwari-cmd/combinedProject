package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.GenaralServiceAuthorizationResData;
import com.digi.common.domain.model.dto.GeneralServiceAuthorizationReq;
import com.digi.common.dto.GenericResponse;

public interface GeneralService {

	GenericResponse<GenaralServiceAuthorizationResData> getGeneralServiceAuthorization(String unit, String channel,
																					   String lang, String serviceId, GeneralServiceAuthorizationReq request);
	
}
