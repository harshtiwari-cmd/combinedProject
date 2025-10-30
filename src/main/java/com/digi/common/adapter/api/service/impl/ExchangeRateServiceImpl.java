package com.digi.common.adapter.api.service.impl;

import com.digi.common.adapter.api.service.ExchangeRateService;
import com.digi.common.domain.model.dto.ExRateRequest;
import com.digi.common.domain.model.dto.FxResData;
import com.digi.common.domain.model.dto.SpecialRateRequest;
import com.digi.common.domain.model.dto.SpecialRateResData;
import com.digi.common.dto.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExchangeRateServiceImpl implements ExchangeRateService {

	@Override
	public GenericResponse<List<FxResData>> getfxRate(String unit, String channel, String lang,
													  String serviceId, String moduleId, String subModuleId, String screenId, ExRateRequest request) {

		return null;
	}

	@Override
	public GenericResponse<SpecialRateResData> getSpecialRate(String unit, String channel, String lang, String serviceId, String moduleId, String subModuleId, String screenId, SpecialRateRequest request) {

		return null;
	}

}
