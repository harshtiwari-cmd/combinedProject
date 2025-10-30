package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.PersonalizationResponse;
import com.digi.common.dto.GenericResponse;

import java.util.Map;

public interface ProfileRestService {

    GenericResponse<Object> updateNickNameAndPictureForProfile(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId, String nickname, String profilepic);

    GenericResponse<Map<String, PersonalizationResponse>> viewProfile(String unit, String channel, String lang, String serviceId, String screenId, String moduleId, String subModuleId);
}
