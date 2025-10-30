package com.digi.common.adapter.api.service;

import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.TermsAndConditionDto;

import java.util.List;

public interface TermAndConditionService {

    GenericResponse<List<TermsAndConditionDto>> getTermsAndConditions(String unit, String channel, String lang, String serviceId, String screenId);
}