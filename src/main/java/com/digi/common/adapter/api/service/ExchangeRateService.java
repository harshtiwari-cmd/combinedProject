package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.ExRateRequest;
import com.digi.common.domain.model.dto.FxResData;
import com.digi.common.domain.model.dto.SpecialRateRequest;
import com.digi.common.domain.model.dto.SpecialRateResData;
import com.digi.common.dto.GenericResponse;

import java.util.List;

public interface ExchangeRateService {

	GenericResponse<List<FxResData>> getfxRate(String unit, String channel, String lang, String serviceId,
											   String moduleId, String subModuleId, String screenId, ExRateRequest request);

	GenericResponse<SpecialRateResData> getSpecialRate(String unit, String channel, String lang, String serviceId,
													   String moduleId, String subModuleId, String screenId, SpecialRateRequest request);
}
