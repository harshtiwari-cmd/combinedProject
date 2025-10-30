package com.digi.common.adapter.api.service;

import com.digi.common.domain.model.dto.RequestDto;
import com.digi.common.domain.model.dto.RuleDTO;
import com.digi.common.dto.GenericResponse;

import java.util.List;

public interface UserService {
    GenericResponse<List<RuleDTO>> getRules(String type, String lang, RequestDto requestDto);

}
 