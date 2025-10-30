package com.digi.common.adapter.api.service;

import com.digi.common.dto.GenericResponse;
import com.digi.common.dto.LabelListRes;

public interface I18Service {

	GenericResponse<LabelListRes> labelList(String unit, String channel, String lang);

}
